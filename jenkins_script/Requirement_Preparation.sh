#!/bin/bash

#Create JSON file for Server and DB IP
SERVER_ID="/tmp/server_id.txt"
DB_ID="/tmp/db_id.txt"
JSONFILE=$2/instance_ip.json

srvrinstid=$(cat $SERVER_ID)
SERVER_INSTANCEIP=`sudo aws ec2 describe-instances --instance-ids $srvrinstid | grep PRIVATEIPADDRESSES |  awk '{print $4}'`
echo "SERVER IP is: $SERVER_INSTANCEIP"

dbinstid=$(cat $DB_ID)
DB_INSTANCEIP=`sudo aws ec2 describe-instances --instance-ids $dbinstid | grep PRIVATEIPADDRESSES |  awk '{print $4}'`
echo "DB IP is: $DB_INSTANCEIP"

sudo touch $JSONFILE	
sudo chmod 775 $JSONFILE
> $JSONFILE

sudo echo "{" 															    >> $JSONFILE
sudo echo "	\"jenkins_server_instance_ip\": \"$SERVER_INSTANCEIP\"", 		>> $JSONFILE
sudo echo "	\"jenkins_db_instance_ip\": \"$DB_INSTANCEIP\""	 		        >> $JSONFILE
sudo echo "}" 															    >> $JSONFILE

cd $WORKSPACE/jenkins_script
sudo chmod 775 chef-solo_prep.sh

#Copy Json files to fmw server
sudo su -c "scp -i $1 -qo StrictHostKeyChecking=no -p $2/instance_ip.json ec2-user@${SERVER_INSTANCEIP}:/tmp" root

#Copy chef-solo_prep.sh to fmw server
sudo su -c "scp -i $1 -qo StrictHostKeyChecking=no -p $2/jenkins_script/chef-solo_prep.sh ec2-user@${SERVER_INSTANCEIP}:/tmp" root

#Run chef-solo_prep.sh to fmw server
ssh -tt ec2-user@${SERVER_INSTANCEIP} -o StrictHostKeyChecking=no -i $1 'sudo su -c "cd /tmp && ./chef-solo_prep.sh" root'

# Run Chef cookbook to prepare requirements needed 
ssh -tt ec2-user@${SERVER_INSTANCEIP} -o StrictHostKeyChecking=no -i $1 'sudo su -c "cd /etc/chef/chef-repo && chef-solo -c solo.rb -o oracle_fmw::prep -j /tmp/instance_ip.json" root'



