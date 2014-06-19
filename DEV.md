First swipe at releasing code to compile and create font packages.

fontcustom is the main dependency.  The version I have installed on my Linux box:

fontcustom (1.0.0, 0.1.4)

./compile.sh will compile the font into svg/fontcustom  There is a preview.html file in there
which will display how the icons render.

I found that you do need to tweak your source .svg files quite a bit to get fontcustom to 
correctly produce a font. For example, you may need to tweak the xml of the .svg to limit how its
painted, named, etc.  Every other .svg file in this package is an example of ones that work. If
you are having issues compiling a font, you may need to tweak the svg to get rid of any advanced
formatting, etc. to get it to compile.

Best of luck,
Joe
