#!/bin/sh

# always delete fontcustom dir first (causes issues on re-compiles)
echo "Removing old compiled fonts..."
rm -Rf font-mfizz

echo "Compiling new fonts..."
fontcustom compile --config=config.yml svg
