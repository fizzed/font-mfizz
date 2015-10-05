#!/bin/sh

VERSION="2.0-SNAPSHOT"
BUILD_DIR="font-mfizz"
DIST_DIR="../font"

rm .fontcustom-manifest.json

./compile.sh

echo "Creating better main css file..."
cat > $BUILD_DIR/font-mfizz.new.css <<EOF
/*
 * Font Mfizz v$VERSION
 * Copyright 2013-2015 Fizzed, Inc.
 * MIT License
 *
 * Project: http://fizzed.com/oss/font-mfizz
 *
 * The font designed for technology and software geeks representing programming
 * languages, operating systems, software engineering, and technology.
 *
 * Fizzed, Inc.
 * Web: http://fizzed.com/
 * Twitter: http://twitter.com/fizzed_inc
 *
 * Joe Lauer
 * Web: http://lauer.bz/
 * Twitter: http://twitter.com/jjlauer
 */

EOF

# grab css contents starting with @font-face
sed -n -e '/@font-face/,$p' $BUILD_DIR/font-mfizz.css >> $BUILD_DIR/font-mfizz.new.css

# change font name from font-mfizz to "FontMfizz"
sed -i 's/"font-mfizz"/"FontMfizz"/' $BUILD_DIR/font-mfizz.new.css

# append css fixes
# Couldn't get this to work.
# ./create-css-fixes.py >> $BUILD_DIR/font-mfizz.new.css

mv $BUILD_DIR/font-mfizz.new.css $BUILD_DIR/font-mfizz.css

echo "Fixing preview html"
mv $BUILD_DIR/font-mfizz-preview.html $BUILD_DIR/preview.html

rm .fontcustom-manifest.json
rm -rf $DIST_DIR
mv $BUILD_DIR $DIST_DIR
