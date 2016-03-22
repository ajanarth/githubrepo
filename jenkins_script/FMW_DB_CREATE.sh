#!/bin/bash

sudo touch $1/db_id.txt
cd $1/chef-repo
sudo  chef-solo -c solo.rb -o launch_ec2::fmw_createdb -j $1/instance_name.json || exit 1

