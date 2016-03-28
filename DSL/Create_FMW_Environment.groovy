freeStyleJob('Environment_Provisioning/Create_FMW_Environment') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps {
    shell('''#!/bin/bash
cd provision-oracle-fmw

export ANSIBLE_FORCE_COLOR=true

# Provision
ansible-playbook launch_ami.yml -e "instance_name=${ENVIRONMENT}_FMW_SERVER aws_region=${AWS_REGION} security_group=${AWS_SECURITY_GROUP_NAME} key_pair=${AWS_KEY_PAIR} vpc_subnet_id=${AWS_SUBNET_ID} ami_id=${AWS_RHEL_AMI_ID} instance_type=t2.large "

# Error handling
if [ $? -gt 0 ]
then
 ansible-playbook terminate_instances.yml
 exit 1
fi 

		''')
    }
	
	wrappers {
        preBuildCleanup()
        injectPasswords()
        maskPasswords()
        sshAgent("ansible-user-key")
        credentialsBinding {
            usernamePassword("AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY", "aws-environment-provisioning")
        }
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