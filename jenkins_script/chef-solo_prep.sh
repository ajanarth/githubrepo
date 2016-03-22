#!/bin/bash
# update the linux server
sudo yum update -y

# install the git on the server
sudo yum install -y git

# install the chef-solo on the server
curl -L https://www.opscode.com/chef/install.sh | bash

# create chef directory
sudo mkdir -p /etc/chef/chef-repo/cookbooks

# download the chef-repo
cd /etc/chef/chef-repo/cookbooks
sudo git clone http://john.smith:Password01@10.0.3.70/gerrit/cookbooks/oracle_fmw

# create the solo.rb for chef config file
cd /etc/chef/chef-repo
sudo touch solo.rb
echo 'file_cache_path "/etc/chef"'                               >  solo.rb
echo 'cookbook_path "/etc/chef/chef-repo/cookbooks"' >> solo.rb
