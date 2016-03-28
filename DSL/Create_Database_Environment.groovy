freeStyleJob('Environment_Provisioning/Create_Database_Environment') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10) 
    label('docker')
    steps {
    shell('''#!/bin/bash
				#cd $WORKSPACE/jenkins_script
				#sudo ./FMW_DB_CREATE.sh $WORKSPACE

				#Create Oracle database to be used in FMW installation
				#cd $WORKSPACE/chef-repo
				#chef-solo -c solo.rb -o launch_ec2::fmw_createdb -j $WORKSPACE/instance_name.json || exit 1
				cd provision-oracle-fmw
				ansible-playbook launch_ami.yml -e "
instance_name=$FMW_SERVER_NAME\
aws_region=$SERVER_AWS_REGION\
key_name= $SERVER_KEY_NAME\
security_group=$SERVER_SECURITY_GROUP\
instance_type=$SERVER_INSTANCE_TYPE\
ami_id=$SERVER_AMI_ID\
volume_type=$SERVER_VOLUME_TYPE\
volume_size=$SERVER_VOLUME_SIZE\
vpc_subnet_id=$SERVER_VPC_SUBNET_ID"
		''')
    }
	
}