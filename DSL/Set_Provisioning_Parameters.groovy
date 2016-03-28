	freeStyleJob('Environment_Provisioning/Set_Provisioning_Parameters') {
    logRotator(-1, 10)
	
    parameters {
        	
		stringParam('FMW_DB_NAME', '', 'Fusion MiddleWare database name')
		stringParam('DB_ANSIBLE_USER', 'ec2-user', 'user to run the ansible')
		stringParam('DB_AWS_REGION', 'us-east-1', 'region where the instance will be created')
		stringParam('DB_KEY_NAME', 'fmw_oracle', 'key pair that will be used by the instance')
		stringParam('DB_SECURITY_GROUP', '', 'security group that will be used by the instance')
		stringParam('DB_INSTANCE_TYPE', '', 'instance type for of the instance')
		stringParam('DB_AMI_ID', 'ami-c46e52ae', 'ami id of the ami that will be cloned')
		stringParam('DB_VOLUME_TYPE', 'gp2', '')
		stringParam('DB_VOLUME_SIZE', '150', 'volume size of the instance')
		stringParam('DB_VPC_SUBNET_ID', 'subnet-3e660d48', 'vpc subnet of the instance')
		
		stringParam('FMW_SERVER_NAME', '', 'Fusion MiddleWare server name')
		stringParam('SERVER_ANSIBLE_USER', 'ec2-user', 'user to run the ansible')
		stringParam('SERVER_AWS_REGION', 'us-east-1', 'region where the instance will be created')
		stringParam('SERVER_KEY_NAME', 'fmw_oracle', 'key pair that will be used by the instance')
		stringParam('SERVER_SECURITY_GROUP', '', 'security group that will be used by the instance')
		stringParam('SERVER_INSTANCE_TYPE', '', 'instance type for of the instance')
		stringParam('SERVER_AMI_ID', 'ami-c46e52ae', 'ami id of the ami that will be cloned')
		stringParam('SERVER_VOLUME_TYPE', 'gp2', '')
		stringParam('SERVER_VOLUME_SIZE', '150', 'volume size of the instance')
		stringParam('SERVER_VPC_SUBNET_ID', 'subnet-3e660d48', 'vpc subnet of the instance')
		
    }
    multiscm {

        git {
         
            remote {
                url('git@gitlab:root/platform-management.git')
              credentials('adop-jenkins-master')
               branch("*/master")
            }
            extensions {
              relativeTargetDirectory('$WORKSPACE')
            }
        }
    }
    label('docker')
    steps {
		shell('''#!/bin/bash 
			# cd $WORKSPACE
			#./FMW_Instance_Create.sh $WORKSPACE
			cd $WORKSPACE/githubrepo
			sudo cp -R chef-repo $WORKSPACE
			sudo cp -R jenkins_script $WORKSPACE
			sudo cp -R provision-oracle-fmw $WORKSPACE
									
			## Create the chef-solo configuration file
			touch 'chef-repo/solo.rb'
			echo 'file_cache_path "'"$WORKSPACE/chef-repo"'"' > 'chef-repo/solo.rb'
			echo 'cookbook_path "'"$WORKSPACE/chef-repo/cookbooks"'"' >> 'chef-repo/solo.rb'
			
			#cd $WORKSPACE/jenkins_script
			chmod 775 chef-solo_prep.sh
			
			## Create a blank json file for the chef solo jenkins parameters. This will be used in the Ruby Execute shell below
			touch 'instance_name.json'
			chmod a+rw 'instance_name.json'
			''')
		ruby('''
			#!/usr/bin/ruby
			require 'json'
			FMW_DB_NAME=`echo $FMW_DB_NAME`.strip
			FMW_SERVER_NAME=`echo $FMW_SERVER_NAME`.strip
                        SECURITY_GROUP_ID=`echo $SECURITY_GROUP_ID`.strip
                        SUBNET_ID=`echo $SUBNET_ID`.strip
			WORKSPACE=`echo $WORKSPACE/instance_name.json`.strip
			jsonfile = {
				"jenkins_db_instance_name" => [FMW_DB_NAME],
			"jenkins_server_instance_name" => [FMW_SERVER_NAME],
                        "security_group" => [SECURITY_GROUP_ID],
                        "subnet_id" => [SUBNET_ID]
			}
			File.open(WORKSPACE,"w+") do |f|
			f.write(JSON.pretty_generate(jsonfile))
			end 
			''')
 	
    }
	wrappers {
      preBuildCleanup() 
	}
    publishers {
			downstreamParameterized {
					trigger('Create_FMW_Environment') {
								condition('SUCCESS')
                      parameters{
                        currentBuild()
						predefinedProp('CUSTOM_WORKSPACE', '$WORKSPACE')
						predefinedProp('AWS_PEM', '/etc/fmw_oracle.pem')
											
						predefinedProp('FMW_SERVER_NAME', '$FMW_SERVER_NAME')
						predefinedProp('SERVER_ANSIBLE_USER', '$SERVER_ANSIBLE_USER')
						predefinedProp('SERVER_AWS_REGION', '$SERVER_AWS_REGION')
						predefinedProp('SERVER_KEY_NAME', '$SERVER_KEY_NAME')
						predefinedProp('SERVER_SECURITY_GROUP', '$SERVER_SECURITY_GROUP')
						predefinedProp('SERVER_INSTANCE_TYPE', '$SERVER_INSTANCE_TYPE')
						predefinedProp('SERVER_AMI_ID', '$SERVER_AMI_ID')
						predefinedProp('SERVER_VOLUME_TYPE', '$SERVER_VOLUME_TYPE')
						predefinedProp('SERVER_VOLUME_SIZE', '$SERVER_VOLUME_SIZE')
						predefinedProp('SERVER_VPC_SUBNET_ID', '$SERVER_VPC_SUBNET_ID')
                      }//propertiesFile('$WORKSPACE/build.properties', false)                     			
				}
			}
      		downstreamParameterized {
					trigger('Create_Database_Environment') {
								condition('SUCCESS')
                      parameters{
                        currentBuild()
						predefinedProp('CUSTOM_WORKSPACE', '$WORKSPACE')
						predefinedProp('AWS_PEM', '/etc/fmw_oracle.pem')
						
						predefinedProp('FMW_DB_NAME', '$FMW_DB_NAME')
						predefinedProp('DB_ANSIBLE_USER', '$DB_ANSIBLE_USER')
						predefinedProp('DB_AWS_REGION', '$DB_AWS_REGION')
						predefinedProp('DB_KEY_NAME', '$DB_KEY_NAME')
						predefinedProp('DB_SECURITY_GROUP', '$DB_SECURITY_GROUP')
						predefinedProp('DB_INSTANCE_TYPE', '$DB_INSTANCE_TYPE')
						predefinedProp('DB_AMI_ID', '$DB_AMI_ID')
						predefinedProp('DB_VOLUME_TYPE', '$DB_VOLUME_TYPE')
						predefinedProp('DB_VOLUME_SIZE', '$DB_VOLUME_SIZE')
						predefinedProp('DB_VPC_SUBNET_ID', '$DB_VPC_SUBNET_ID')
                      }//propertiesFile('$WORKSPACE/build.properties', false)    			
				}
			}
		}
  }