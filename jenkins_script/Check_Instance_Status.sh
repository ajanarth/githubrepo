#!/bin/bash

cd $1/chef-repo
sudo chef-solo -c solo.rb -o launch_ec2::check_status || exit 1
