#!/usr/bin/env python2.7

#
# This script will generate code to plop into font-mfizz article for mfizz.com
#
# sudo pip install PyYAML
# sudo pip install requests
# 

import sys, os, getopt
sys.dont_write_bytecode = True
import json

glyph_list = json.load(open("svg/fontcustom/icons.json", "rb"))
glyphs = set()

for g in glyph_list:
    glyphs.add(g)

css_fix = "\n/* These classes only added to fix FontFamily to display FontMfizz during debug/inspection */\n"

for glyph in glyphs:
    css_fix += '.icon-' + glyph + ",\n"

# chop off last 2 chars
css_fix = css_fix[:-2]

css_fix += "{\n  font-family: \"FontMfizz\";\n}"

print css_fix
