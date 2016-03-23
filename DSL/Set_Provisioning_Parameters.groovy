	freeStyleJob('Environment_Provisioning/Set_Provisioning_Parameters') {
    logRotator(-1, 10)
	
    parameters {
        stringParam('FMW_DB_NAME', '', 'Fusion MiddleWare database name')
      	stringParam('FMW_SERVER_NAME', '', 'Fusion MiddleWare server name')
		stringParam('SUBNET_ID', '', 'Fusion MiddleWare database name')
      	stringParam('SECURITY_GROUP_ID', '', 'Fusion MiddleWare server name')
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
                      }//propertiesFile('$WORKSPACE/build.properties', false)
                      			
					}
			}
    }
  }