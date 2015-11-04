Font Mfizz by Fizzed
=======================================

 - [Fizzed, Inc.](http://fizzed.com) (Follow on Twitter: [@fizzed_inc](http://twitter.com/fizzed_inc))

## Vector Icons for Technology and Software Geeks

Font Mfizz provides scalable vector icons representing programming languages,
operating systems, software engineering, and technology. It was designed designed
for technology and software geeks. It can be customized — size, color, drop shadow,
and anything that can be done with the power of CSS.

[View icons in action and usage information](http://fizzed.com/oss/font-mfizz)

Designed by [Fizzed, Inc.](http://fizzed.com/)

Updates tweeted [@fizzed_inc](http://twitter.com/fizzed_inc)

## Releases

[Download packages](https://github.com/fizzed/font-mfizz/releases)

## Can I get an icon added?

Do you want a new icon added?  Since this font is a hobby, the fastest way to get a new
one added is to add it yourself.  We're happy to accept pull requests and periodically publish
new builds.  Here are the steps to get an icon officially added:

1. Does the icon fit within the theme of this font?  Does it represent a programming language,
operating system, or software engineering?

2. Find (or create) a .svg (Scalable Vector Graphics) version of the icon you'd like to add.
Please note that you may need to tweak and simplify your .svg file for it to be properly
converted into a web font.

3. [Install the build dependencies](DEVELOPMENT.md) to compile the .svg files into a web font.

4. Add your .svg file to `src/svg`. The file name will become the eventual glyph name (e.g. java.svg
becomes .icon-java in the css).

5. In your shell run `java -jar blaze.jar compile server`

6. Visit `http://localhost:8080/preview.html` in your browser

7. Submit a pull request. Do NOT include any files from `dist`.



