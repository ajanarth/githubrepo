freeStyleJob('Environment_Provisioning/Create_FMW_Environment') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps {
    shell('''#!/bin/bash
				#cd $WORKSPACE/jenkins_script
				#sudo ./FMW_SERVER_CREATE.sh $WORKSPACE

				## Run chef cookbook to create a blank aws instance to be used as FMW server.
				#cd $WORKSPACE/chef-repo
				#chef-solo -c solo.rb -o launch_ec2::fmw_createserver -j $WORKSPACE/instance_name.json || exit 1
		cd provision-oracle-fmw
		ansible-playbook launch_ami.yml -e "

instance_name=$FMW_SERVER_NAME\
aws_region=$DB_AWS_REGION\
key_name= $DB_KEY_NAME\
security_group=$DB_SECURITY_GROUP\
instance_type=$DB_INSTANCE_TYPE\
ami_id=$DB_AMI_ID\
volume_type=$DB_VOLUME_TYPE\
volume_size=$DB_VOLUME_SIZE\
vpc_subnet_id=$DB_VPC_SUBNET_ID"
		
		''')
    }
    publishers {
		downstreamParameterized {
			trigger('Check_Instance_Status') {
				condition('SUCCESS')
                   parameters{
                       currentBuild()
						predefinedProp('CUSTOM_WORKSPACE', '$WORKSPACE')
						predefinedProp('AWS_PEM', '/etc/fmw_oracle.pem')						
                   }//propertiesFile('$WORKSPACE/build.properties', false)                      			
			}
		}
    }
}