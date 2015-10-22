import static com.fizzed.blaze.Shells.*
import org.unix4j.Unix4j
import org.unix4j.unix.Tail
import java.io.File

// required executables
requireExec("fontcustom", "Visit https://github.com/fizzed/font-mfizz/blob/master/DEVELOPMENT.md").run()

// configuration
name = config.getString("font.name")
version = config.getString("font.version")
targetDir = context.withBaseDir(config.getString("font.target.dir"))
buildDir = context.withBaseDir(config.getString("font.build.dir"))
distDir = context.withBaseDir(config.getString("font.dist.dir"))
srcDir = context.withBaseDir("src")
fontcustomConfigFile = context.withBaseDir("src/config.yml")
svgDir = context.withBaseDir("src/svg")
year = Calendar.getInstance().get(Calendar.YEAR);

log.info("Building {} version {}", name, version)
log.info("Build to {}", buildDir)
log.info("Dist to {}", distDir)

def clean() {
    log.info("Deleting dir {}", targetDir)
    exec("rm", "-Rf", targetDir).run()
    exec("rm", "-Rf", context.withBaseDir(".fontcustom-manifest.json")).run()
}

def font_compile() {
    clean()

    // verify fontcustom version
    fontcustomVersion = exec("fontcustom", "-v").readOutput().run().output().trim()
    
    if (!fontcustomVersion.contains("1.3.9")) {
        log.warn("Detected {}! This script only confirmed to work with 1.3.8", fontcustomVersion)
    }
    
    log.info("Compiling glyphs...")
    exec("fontcustom", "compile", "--config=" + fontcustomConfigFile, svgDir).run()
    
    // move the .fontcustom-manifest.json to the right spot
    jsonManifestFile = context.withBaseDir('.fontcustom-manifest.json')
    newJsonManifestFile = new File(targetDir, jsonManifestFile.getName())
    jsonManifestFile.renameTo(newJsonManifestFile)
}

def compile() {
    font_compile()
    
    log.info("Creating improved stylesheet...")
    
    headerFile = new File(srcDir, "header.txt")
    cssFile = new File(buildDir, "font-mfizz.css")
    newCssFile = new File(buildDir, "font-mfizz.new.css")
    
    // stip first 4 lines of css to new css
    Unix4j
        .tail(Tail.Options.s, 4, cssFile)
        .toFile(newCssFile)
    
    // cat header and new css to old css
    Unix4j
        .cat(headerFile, newCssFile)
        .sed('s/\\$\\{VERSION\\}/' + version + '/')
        .sed('s/\\$\\{YEAR\\}/' + year + '/')
        .sed('s/"font-mfizz"/"FontMfizz"/')
        .toFile(cssFile)
    
    oldPreviewFile = new File(buildDir, "font-mfizz-preview.html")
    newPreviewFile = new File(buildDir, "preview.html")
    
    oldPreviewFile.renameTo(newPreviewFile)
    
    log.info("Visit file://{}", newPreviewFile.getCanonicalPath())
}
