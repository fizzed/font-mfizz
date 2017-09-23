sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update
sudo apt-get -y install ruby ruby-dev fontforge ttfautohint unzip libz-dev software-properties-common python-software-properties cmake build-essential oracle-java8-installer
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo update-java-alternatives -s java-8-oracle

wget https://github.com/Jan-LucaKlees/sfnt2woff/archive/master.zip
unzip master.zip
cd sfnt2woff-master
cmake .
sudo make install
sudo mv sfnt2woff /usr/local/bin/
cd ..
rm -Rf sfnt2woff-master master.zip

# finally fontcustom (can we lock down specific ver?)
echo "Install ruby gem fontcustom v1.3.8 (may take awhile)..."
sudo gem install fontcustom -v 1.3.8
