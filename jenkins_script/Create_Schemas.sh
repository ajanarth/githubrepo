#!/bin/bash

SERVER_ID="/opt/Software/devtools/server_id.txt"
srvrinstid=$(cat $SERVER_ID)
SERVER_INSTANCEIP=`sudo aws ec2 describe-instances --instance-ids $srvrinstid | grep PRIVATEIPADDRESSES |  awk '{print $4}'`
echo "SERVER IP is: $SERVER_INSTANCEIP"

ssh -tt ec2-user@${SERVER_INSTANCEIP} -o StrictHostKeyChecking=no -i $1 'sudo su -c "cd /etc/chef/chef-repo && chef-solo -c solo.rb -o oracle_fmw::rcu_create" root'




