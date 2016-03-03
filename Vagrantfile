# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  # oracle java 8: arg is jre, server-jre, or jdk
  #config.vm.provision "shell", path: "bootstrap-java8.sh", args: "jre"
  # fontcustom
  config.vm.provision "shell", path: "src/vagrant/bootstrap-fontcustom.sh"  
end

Vagrant::Config.run do |config|
  config.vm.box = "minimal/trusty64"
  config.vm.host_name = "font-mfizz"
end
