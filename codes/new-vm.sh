#!/bin/bash

cd ~

sudo apt update
sudo apt install -y openjdk-8-jdk-headless git zsh curl

# Start docker installation
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo apt-key fingerprint 0EBFCD88

sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"

sudo apt install -y docker-ce docker-ce-cli containerd.io

sudo usermod -aG docker ${USER}

# Finish docker installation



sh -c "$(curl -fsSL https://raw.github.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
sed -i '/ZSH_THEME=/c ZSH_THEME="crunch"' .zshrc
sudo usermod --shell $(which zsh) ${USER}
mkdir -p project
cd project
git clone https://github.com/lakinduakash/ballerina-lang
git clone https://github.com/lakinduakash/ballerina-microbenchmarks