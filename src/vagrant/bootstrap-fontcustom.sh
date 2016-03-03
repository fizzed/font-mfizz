sudo apt-get update
sudo apt-get -y install ruby ruby-dev fontforge ttfautohint unzip libz-dev software-properties-common python-software-properties

# get latest build
wget http://people.mozilla.com/~jkew/woff/woff-code-latest.zip
unzip -d woff woff-code-latest.zip
cd woff
make
sudo mv sfnt2woff /usr/local/bin/
cd ..
rm -Rf woff woff-code-latest.zip

# finally fontcustom (can we lock down specific ver?)
echo "Install ruby gem fontcustom v1.3.8 (may take awhile)..."
sudo gem install fontcustom -v 1.3.8

# finish off with java 8
sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo apt-get install -y oracle-java8-installer
sudo update-java-alternatives -s java-8-oracle
