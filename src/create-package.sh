#!/bin/sh

VERSION="1.4-SNAPSHOT"
BUILD_DIR="font-mfizz"
DIST_DIR="font"

rm -Rf $DIST_DIR
mkdir $DIST_DIR

echo "Copying custom font..."
cp -Rv svg/* $BUILD_DIR/

echo "Creating better main css file..."
cat > $BUILD_DIR/font-mfizz.new.css <<EOF
/*
 * Font Mfizz v$VERSION
 * Copyright 2013 Mfizz Inc, Joe Lauer
 * MIT License
 *
 * Project: http://mfizz.com/oss/font-mfizz
 *
 * The font designed for technology and software geeks representing programming
 * languages, operating systems, software engineering, and technology.
 *
 * Mfizz Inc
 * Web: http://mfizz.com/
 * Twitter: http://twitter.com/mfizz_inc
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
./create-css-fixes.py >> $BUILD_DIR/font-mfizz.new.css

mv $BUILD_DIR/font-mfizz.new.css $BUILD_DIR/font-mfizz.css


echo "Fixing preview html"
mv $BUILD_DIR/fontcustom-preview.html $BUILD_DIR/preview.html
sed -i 's/fontcustom.css/font-mfizz.css/g' $BUILD_DIR/preview.html

echo "copying license..."
cp LICENSE.txt $BUILD_DIR/
cp README.md $BUILD_DIR/
cp RELEASE-NOTES.md $BUILD_DIR/

tar cvfz $BUILD_DIR.tar.gz $BUILD_DIR
