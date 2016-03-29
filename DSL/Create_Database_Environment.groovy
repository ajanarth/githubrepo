freeStyleJob('Environment_Provisioning/Create_Database_Environment') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10) 
    label('docker')
    steps {
    shell('''#!/bin/bash

cd provision-oracle-fmw

export ANSIBLE_FORCE_COLOR=true

# Provision
ansible-playbook launch_ami.yml -e "instance_name='${ENVIRONMENT}_DATABASE_SERVER' aws_region=${AWS_REGION} security_group=${AWS_SECURITY_GROUP_NAME} key_name=${AWS_KEY_PAIR} vpc_subnet_id=${AWS_SUBNET_ID} ami_id=${AWS_DB_AMI_ID} instance_type=m3.xlarge"
echo $(cat instace_ids.txt) > /tmp/db_id.txt
# Error handling
if [ $? -gt 0 ]
then
 ansible-playbook terminate_instances.yml
 exit 1
fi 


		''')
    }
	wrappers {
        injectPasswords()
        maskPasswords()
        sshAgent("ansible-user-key")
        credentialsBinding {
            usernamePassword("AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY", "aws-environment-provisioning")
        }
	}
	publishers {
		downstreamParameterized {
			trigger('Requirement_Preparation') {
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