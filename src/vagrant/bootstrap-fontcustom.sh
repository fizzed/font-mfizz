sudo apt-get update
sudo apt-get -y install ruby ruby-dev fontforge ttfautohint unzip libz-dev software-properties-common python-software-properties cmake build-essential

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
sudo LC_CTYPE=en_US.UTF-8 LANG=en_US.UTF-8 gem install fontcustom -v 1.3.8

# finish off with java 8
sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo apt-get install -y oracle-java8-installer
sudo update-java-alternatives -s java-8-oracle