# Development process

How to to compile and create font packages.

## Prerequisites

* Ruby 1.9.x or greater.
* Python 3 ????

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

    sudo apt-get install ruby1.*.*-dev.

Then install the fontcustom dependencies:

    sudo apt-get install fontforge ttfautohint
    wget http://people.mozilla.com/~jkew/woff/woff-code-latest.zip
    unzip woff-code-latest.zip -d sfnt2woff
    cd sfnt2woff
    make
    sudo mv sfnt2woff /usr/local/bin/
    gem install fontcustom

If `ttfautohint` is not available in your distribution,
Font Custom will still work but your generated font will
not be properly hinted.

## Preparing the artwork

The simplest way to get the sizing right for the artwork is to open one of the
existing SVN files into your editor (Adobe Illustrator, InkScape), save a
copy of it with the new name, delete the current artwork, then paste yours in,
scale to the artboard size.

## Building the font

Execute this:

    ./create-package.sh

This will re-generate the font directory in the root of the repo.


## Notes

`./compile.sh` will compile the font into `font-mfizz`

There is a `preview.html` file in there which will display how the icons render.

You may need to tweak your source `.svg` files quite a bit to get fontcustom to correctly produce a font.

For example, you may need to tweak the xml of the `.svg` to limit how its painted, named, etc.

Every other `.svg` file in this package is an example of ones that work.

If you are having issues compiling a font, you may need to tweak the
SVG to get rid of any advanced formatting, etc. to get it to compile.
