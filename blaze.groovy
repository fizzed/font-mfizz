import com.fizzed.blaze.Contexts
import static com.fizzed.blaze.Contexts.withBaseDir
import static com.fizzed.blaze.Systems.*
import org.unix4j.Unix4j
import org.unix4j.unix.Tail
import java.io.File
import java.nio.file.Files
import java.time.LocalDate
// for embedded undertow
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import io.undertow.server.handlers.resource.PathResourceManager
import java.nio.file.Paths
import static io.undertow.Handlers.resource

// required executables
requireExec("fontcustom", "Visit https://github.com/fizzed/font-mfizz/blob/master/DEVELOPMENT.md").run()

// configuration
context = Contexts.currentContext()
config = context.config()
log = context.logger()
name = config.find("font.name").get()
version = config.find("font.version").get()
buildDir = withBaseDir(config.find("font.build.dir").get())
distDir = withBaseDir(config.find("font.dist.dir").get())
srcDir = withBaseDir("src")
fontcustomConfigFile = withBaseDir("src/config.yml")
svgDir = withBaseDir("src/svg")
year = LocalDate.now().getYear();

log.info("Will create {} version {}", name, version)
log.info("Will build to {}", buildDir)
log.info("Will dist to {}", distDir)

def clean() {
    log.info("Deleting dir {}", buildDir)
    remove(buildDir).recursive().force().run()
    remove(withBaseDir(".fontcustom-manifest.json")).recursive().force().run()
}

def font_compile() {
    clean()

    // verify fontcustom version
    fontcustomVersion = exec("fontcustom", "-v").captureOutput().run().output().trim()
    
    if (!fontcustomVersion.contains("1.3.8")) {
        log.warn("Detected {}! This script only confirmed to work with 1.3.8", fontcustomVersion)
    }
    
    log.info("Compiling glyphs...")
    exec("fontcustom", "compile", "--config=" + fontcustomConfigFile, svgDir).run()
    
    // move the .fontcustom-manifest.json to the right spot
    jsonManifestFile = context.withBaseDir('.fontcustom-manifest.json')
    newJsonManifestFile = buildDir.resolve(jsonManifestFile.getFileName())
    Files.move(jsonManifestFile, newJsonManifestFile)
}

def compile() {
    font_compile()
    
    log.info("Creating improved stylesheet...")
    
    headerFile = srcDir.resolve("header.txt")
    cssFile = buildDir.resolve("font-mfizz.css")
    newCssFile = buildDir.resolve("font-mfizz.new.css")
    
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
    
    oldPreviewFile = buildDir.resolve("font-mfizz-preview.html")
    newPreviewFile = buildDir.resolve("preview.html")
    
    Files.move(oldPreviewFile, newPreviewFile)
    
    log.info("Visit file://{}", newPreviewFile.normalize())
}

def server() {
    def undertow = Undertow.builder()
        .addHttpListener(8080, "localhost")
        .setHandler(resource(new PathResourceManager(buildDir, 100))
            .setDirectoryListingEnabled(true))
        .build()
       
    undertow.start()
    
    log.info("Open browser to http://localhost:8080/preview.html")
    
    // hack for waiting on undertow (wish it could be joined)
    synchronized (undertow) {
        undertow.wait();
    }
}

def dist() {
    log.warn("DO NOT SUBMIT PULL REQUESTS THAT INCLUDE THE 'dist' DIR!!")
    
    log.info("Copying build {} to dist {}", buildDir, distDir)
    remove(distDir).recursive().force().run()
    exec("cp", "-Rf", buildDir, distDir).run()
}

def release() {
    // confirm we are not a snapshot
    if (version.endsWith("-SNAPSHOT")) {
        fail("Version ${version} is a snapshot (change blaze.conf then re-run)")
    }
    
    // confirm release notes contains version
    foundVersion =
        Unix4j
            .fromFile(withBaseDir("RELEASE-NOTES.md"))
            .grep("^#### " + version + " - \\d{4}-\\d{2}-\\d{2}\$")
            .toStringResult()
            
    if (foundVersion == null || foundVersion.equals("")) {
        fail("Version ${version} not present in RELEASE-NOTES.md")
    }
    
    compile()
    dist()
    
    // git commit & tag
    exec("git", "commit", "-am", "Preparing for release v" + version).run()
    exec("git", "tag", "v" + version).run()
    
    log.info("Tagged with git. Please run 'git push -u origin' now.")
}
