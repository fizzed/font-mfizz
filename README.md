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

## Sponsored by

Font Mfizz is proudly sponsored by <a href="https://www.greenback.com">Greenback</a>.  We love the service and think you would too.

<a href="https://www.greenback.com" title="Greenback - Expenses made simple"><img src="https://www.greenback.com/assets/images/logo-greenback.png" height="48" width="166" alt="Greenback"></a>

<a href="https://www.greenback.com" title="Greenback - Expenses made simple">More designing. Less paperwork. Expenses made simple.</a>

## Releases

[Download packages](https://github.com/fizzed/font-mfizz/releases)

## CDN

[cdnjs](https://cdnjs.com) now hosts font-mfizz for use by all. Visit [https://cdnjs.com/libraries/font-mfizz](https://cdnjs.com/libraries/font-mfizz) for more info.

## Development

Building font-mfizz relies on many system-level dependencies.  Rather than you
trying to setup the environment yourself, we have switched to using
[Vagrant](https://www.vagrantup.com/) to help automate the setup of a development environment.
What's great is that even if you're on Linux, Windows, or Mac, you'll get the exact
environment the font-mfizz maintainers use to build the font.

 * Install [Vagrant](https://www.vagrantup.com/)
 * Run `vagrant up`
 * Sit back, get a coffee...
 * Run `vagrant ssh`
 * You're now ssh'ed into your font-mfizz virtual dev machine
 * `cd /vagrant`
 * `java -jar blaze.jar`
 * On your actual real machine, open up `build/font/preview.html` in your browser

## Can I add an icon?

Short answer, yes!

Since this font is a hobby, the fastest way to get a new one added is to add it
yourself.  We're happy to accept pull requests and periodically publish new builds. 
Here are the steps to get an icon officially added:

1. Does the icon fit within the theme of this font?  Does it represent a programming language,
operating system, or software engineering?

2. Find (or create) a .svg (Scalable Vector Graphics) version of the icon you'd like to add.
Please note that you may need to tweak and simplify your .svg file for it to be properly
converted into a web font.

3. Add your .svg file to `src/svg`. The file name will become the eventual glyph name (e.g. java.svg
becomes .icon-java in the css).

4. Follow the steps above to setup your development environment.  Compile the font with your
   new icon(s).  Verify they look good in a browser.  Sometimes they need tweaking.

5. Submit a pull request.

## License

This project is licensed under the MIT License and © 2013-2017 Fizzed, Inc. You can find a copy of
all of the licenses in the projects LICENSE.txt file.

All icons representing commercial companies are trademarks of their respective owners. The use of
these trademarks does not indicate endorsement of the trademark holder by Fizzed, Font Mfizz, nor
vice versa.
