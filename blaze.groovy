#!/usr/bin/env blaze

import com.fizzed.blaze.Contexts
import static com.fizzed.blaze.Contexts.*
import static com.fizzed.blaze.Systems.*
import static com.fizzed.blaze.util.Globber.globber
import com.fizzed.blaze.util.Streamables
import org.unix4j.Unix4j
import org.unix4j.unix.Tail
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
// unix4j
import org.unix4j.Unix4j
import org.unix4j.unix.Tail
// extra file utils
import org.apache.commons.io.FileUtils
// for zipping
import org.zeroturnaround.zip.ZipUtil
import org.zeroturnaround.zip.NameMapper
// for http
import org.apache.http.HttpVersion
import org.apache.http.client.HttpResponseException
import org.apache.http.entity.ContentType
import org.apache.http.client.fluent.Request
// for json
import com.jayway.jsonpath.JsonPath
// for versioning
import com.github.zafarkhaja.semver.Version
// for embedded undertow
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import io.undertow.server.handlers.resource.PathResourceManager
import java.nio.file.Paths
import static io.undertow.Handlers.resource

// configuration
context = Contexts.currentContext()
config = context.config()
log = context.logger()
name = config.value("font.name").get()
version = config.value("font.version").get()
finalName = name + "-" + version
buildDir = withBaseDir(config.value("font.build.dir").get())
fontBuildDir = buildDir.resolve("font")
distDir = withBaseDir(config.value("font.dist.dir").get())
srcDir = withBaseDir("src")
fontcustomConfigFile = srcDir.resolve("config.yml")
svgDir = srcDir.resolve("svg")
finalZipFile = buildDir.resolve(finalName + ".zip")
year = LocalDate.now().getYear()

log.info("Will create {} version {}", name, version)
log.info("Will build to {}", buildDir)
log.info("Will dist to {}", distDir)

def clean() {
    log.info("Deleting dir {}", buildDir)
    remove(buildDir).recursive().force().run()
    remove(withBaseDir(".fontcustom-manifest.json")).recursive().force().run()
}

def font_compile() {
    // required executables
requireExec("fontcustom", "Visit https://github.com/fizzed/font-mfizz/blob/master/DEVELOPMENT.md").run()

    clean()

    // verify fontcustom version
    capture = Streamables.captureOutput()
    exec("fontcustom", "-v").pipeOutput(capture).run()
    fontcustomVersion = capture.toString().trim()
    
    if (!fontcustomVersion.contains("1.3.8")) {
        log.warn("Detected {}! This script only confirmed to work with 1.3.8", fontcustomVersion)
    }
    
    log.info("Compiling glyphs...")
    exec("fontcustom", "compile", "--config=" + fontcustomConfigFile, svgDir).run()
    
    // move the .fontcustom-manifest.json to the right spot
    jsonManifestFile = withBaseDir('.fontcustom-manifest.json')
    newJsonManifestFile = fontBuildDir.resolve(jsonManifestFile.getFileName())
    Files.move(jsonManifestFile, newJsonManifestFile)
}

def compile() {
    font_compile()
    
    log.info("Creating improved stylesheet...")
    
    headerFile = srcDir.resolve("header.txt")
    cssFile = fontBuildDir.resolve("font-mfizz.css")
    newCssFile = fontBuildDir.resolve("font-mfizz.new.css")
    
    // stip first 4 lines of css to new css
    Unix4j
        .tail(Tail.Options.s, 4, cssFile.toFile())
        .toFile(newCssFile.toFile())
    
    // cat header and new css to old css
    Unix4j
        .cat(headerFile.toFile(), newCssFile.toFile())
        .sed('s/\\$\\{VERSION\\}/' + version + '/')
        .sed('s/\\$\\{YEAR\\}/' + year + '/')
        .sed('s/"font-mfizz"/"FontMfizz"/')
        .toFile(cssFile.toFile())
        
    // delete the temp new file
    remove(newCssFile).run()
    
    oldPreviewFile = fontBuildDir.resolve("font-mfizz-preview.html")
    newPreviewFile = fontBuildDir.resolve("preview.html")
    
    Files.move(oldPreviewFile, newPreviewFile)
    
    log.info("Visit file://{}", newPreviewFile.normalize())
}

def server() {
    def undertow = Undertow.builder()
        .addHttpListener(8080, "localhost")
        .setHandler(resource(new PathResourceManager(fontBuildDir, 100))
            .setDirectoryListingEnabled(true))
        .build()
       
    undertow.start()
    
    log.info("Open browser to http://localhost:8080/preview.html")
    
    // hack for waiting on undertow (wish it could be joined)
    synchronized (undertow) {
        undertow.wait()
    }
}

def dist() {
    log.warn("DO NOT SUBMIT PULL REQUESTS THAT INCLUDE THE 'dist' DIR!!")
    
    log.info("Copying build {} to dist {}", fontBuildDir, distDir)
    remove(distDir).recursive().force().run()

    FileUtils.copyDirectory(fontBuildDir.toFile(), distDir.toFile())

    log.info("Removing unnecessary manifest file");
    remove(globber(distDir, ".fontcustom-manifest.json")).run()

    ZipUtil.pack(distDir.toFile(), finalZipFile.toFile(), new NameMapper() {
        public String map(String name) {
            return finalName + "/" + name;
        }
    })
}

def release() {
    requireExec("git").run()

    // confirm we are not a snapshot
    if (version.endsWith("-SNAPSHOT")) {
        fail("Version ${version} is a snapshot (change blaze.conf then re-run)")
    }
    
    // confirm release notes contains version
    foundVersion =
        Unix4j
            .fromFile(withBaseDir("RELEASE-NOTES.md").toFile())
            .grep("^#### " + version + " - \\d{4}-\\d{2}-\\d{2}\$")
            .toStringResult()
            
    if (foundVersion == null || foundVersion.equals("")) {
        fail("Version ${version} not present in RELEASE-NOTES.md")
    }
    
/**
    // any unstaged files in git? (0 = no changes, 1 = changes exist)
    exitValue =
        exec("git", "diff-files", "--quiet").exitValues(0, 1).run()
        
    if (exitValue == 1) {
        fail("Uncommitted changes in git. Commit them first then re-run this task")
    }
*/

    //compile()
    //dist()
    
    // git commit & tag
    exec("git", "commit", "-am", "Preparing for release v" + version).run()
    exec("git", "tag", "v" + version).run()
    exec("git", "push", "-u", "origin", "master").run()
    exec("git", "push", "--tags", "origin").run()
    
    publish_github()
    
    bump_version()
    
    exec("git", "commit", "-am", "Preparing for next development iteration").run()
    exec("git", "push", "-u", "origin", "master").run()
}

def bump_version() {
    semver = Version.valueOf(version)
    semver = semver.incrementMinorVersion()
    newver = semver.toString()
    newver = newver + "-SNAPSHOT"
    log.info("New version {}", newver)
    
    // update blaze.conf with new version
    configFile = withBaseDir("blaze.conf")
    newConfigFile = withBaseDir("blaze.conf.new")
    
    Unix4j
        .cat(configFile.toFile())
        .sed('s/"' + version + '"/"' + newver + '"/')
        .toFile(newConfigFile.toFile())
    
    Files.delete(configFile)
    Files.move(newConfigFile, configFile)
}

def publish_github() {
    // zip file must exist
    if (Files.notExists(finalZipFile)) {
        fail("Final zip " + finalZipFile + " must exist!")
    }
    
    // github token
    githubToken = config.value("github.token").getOrNull()
    
    if (githubToken == null) {
        // prompt for github token to use for pushing release
        githubToken = Contexts.prompt("github token (for pushing release): ")
        
        if (githubToken == null) {
            fail("github token required")
        }
    }

    json = null
    
    try {
         log.info("Trying to create new github release...")
        
        // try to create release
        json =
            Request.Post("https://api.github.com/repos/fizzed/font-mfizz/releases")
                .version(HttpVersion.HTTP_1_1)
                .addHeader("Authorization", "token " + githubToken)
                .addHeader("Accept", "application/json")
                .bodyString("{ \"tag_name\" : \"v" + version + "\" }", ContentType.create("application/json"))
                .execute()
                .returnContent()
                .asString();
                
        log.info("Created new github release!")
    } catch (HttpResponseException e) {
        if (e.getStatusCode() == 422) {
            log.info("Github release already exists, querying for it...")
            
            // query for existing release
            json =
                Request.Get("https://api.github.com/repos/fizzed/font-mfizz/releases/tags/v" + version)
                    .version(HttpVersion.HTTP_1_1)
                    .addHeader("Authorization", "token " + githubToken)
                    .addHeader("Accept", "application/json")
                    .execute()
                    .returnContent()
                    .asString()
                    
            log.info("Found github release!")
        }
    }
    
    // get the assets url
    uploadUrlTemplate = JsonPath.read(json, '$.upload_url')
    
    // strip template at end
    uploadUrl = uploadUrlTemplate.substring(0, uploadUrlTemplate.indexOf("{"))
    
    log.info("Using upload_url {}", uploadUrl)
    
    json =
        Request.Post(uploadUrl + "?name=" + finalZipFile.getFileName())
	     .version(HttpVersion.HTTP_1_1)
	     .useExpectContinue()
             .addHeader("Authorization", "token " + githubToken)
     	     .bodyFile(finalZipFile.toFile(), ContentType.create("application/zip"))
	     .execute()
	     .returnContent()
	     .asString();
    
    log.info("Published {} to github!", finalZipFile)
}
