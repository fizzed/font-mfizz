# Development process

Building font-mfizz relies on many system-level dependencies.  This guide is
the nitty gritty details.  As of March 2016, there is a [Vagrant](https://www.vagrantup.com/)
bootstrapping process to setup the *exact* system required to compile your font.

Its **highly** recommended you simply use the Vagrant-method.  This guide is
included mostly for the maintainers of font-mfizz.
## What you need

 - Java 8
 - Ruby 1.9.x or greater
 - FontCustom 1.3.8 or greater

## Install

FontCustom does most of the heavy-lifting work.

### OSX

    brew update
    brew doctor

Fix any errors shown by `brew doctor`.

    brew install python
    brew install gettext libpng jpeg libtiff giflib cairo pango
    brew install libspiro czmq fontconfig automake libtool pkg-config glib

    brew install fontforge --with-cairo --with-czmq --with-gif --with-x --with-libspiro --with-pango --enable-pyextension --debug --with-python

    brew linkapps

    brew install eot-utils
    brew install ttfautohint
    gem install fontcustom

### Linux

On Debian and Ubuntu, you may need to install the ruby dev packages first
if not already on your machine:

    sudo apt-get install ruby ruby-dev

Then install the fontcustom dependencies:

    sudo apt-get install fontforge ttfautohint
    wget http://people.mozilla.com/~jkew/woff/woff-code-latest.zip
    unzip -d woff woff-code-latest.zip
    cd woff
    make
    sudo mv sfnt2woff /usr/local/bin/
    cd ..
    rm -Rf woff woff-code-latest.zip
    sudo gem install fontcustom

If `ttfautohint` is not available in your distribution,
Font Custom will still work but your generated font will
not be properly hinted.

## Preparing the artwork

The simplest way to get the sizing right for the artwork is to open one of the
existing SVG files into your editor (Adobe Illustrator, InkScape), save a
copy of it with the new name, delete the current artwork, then paste yours in,
scale to the artboard size.

### Tips for editing in Inkscape

Open the file in Inkscape.  You'll want to start removing / editing paths to
simplify the source for working in a single color environment.  Here are 
tips to keep in mind (specifically for Inkscape, but could work in other apps).

- Remove any background layers/paths

- Paths should be fill only, no stroke, and a single color (black is best)

- Edit > XML Editor is your friend.  Some svg files will have too many layers
you won't see unless you get into the xml itself.  You may want to delete
many of them.

- File > Document properties > Resize page to content > 
Resize page to drawing or selection will usually fix the final viewport and
the size of your generated icon.
    
- Save as "Plain SVG"

## Building the font

In your shell

    java -jar blaze.jar

This will re-generate the font build directory in the repo.  Browse a 
preview of your new font by opening this in your browser

    build/preview.html

You can view the file using an embedded webserver

    java -jar blaze.jar compile server

In your browser

    http://localhost:8080/preview.html

## Build issues

The most likely issue is that your version of FontCustom is different than what
the scripts were developed with.  Check your version of FontCustom

    gem list fontcustom

If not v1.3.8 then you can either try that specific version OR figure out what
changed with FontCustom and submit a pull request to work with the newer version.

## Known issues

Due to a limitation with FontCustom, the glyphs are allocated specific Unicode
addresses **alphabetically***.

This means that if you add a new icon, the Unicode addresses
of all the icons that occur alphabetically after it will be different.

Currently, there does **not** appear to be a way to assign static addresses
to glyphs with FontCustom, but there has been some talk in their
Github issues.

This could be a real problem if you are using these icons in a non-CSS
environment.

## Notes

You may need to tweak your source `.svg` files quite a bit to get fontcustom to correctly produce a font.

For example, you may need to tweak the xml of the `.svg` to limit how its painted, named, etc.

Every other `.svg` file in this package is an example of ones that work.

If you are having issues compiling a font, you may need to tweak the
SVG to get rid of any advanced formatting, etc. to get it to compile.

