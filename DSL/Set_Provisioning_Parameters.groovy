	freeStyleJob('Create_Environment_1/Set_Provisioning_Parameters') {
    logRotator(-1, 10)
    parameters {
        stringParam('FMW_DB_NAME', '', 'Fusion MiddleWare database name')
      	stringParam('FMW_SERVER_NAME', '', 'Fusion MiddleWare server name')
    }
    multiscm {

        git {
         
            remote {
                url('http://john.smith@10.0.3.70/gerrit/cookbooks/launch_ec2.git')
              credentials('f5aef68c-243d-400f-b43f-c7abfc714309')
               branch("*/master")
            }
            extensions {
              relativeTargetDirectory('$WORKSPACE/chef-repo/cookbooks/launch_ec2')
            }
        }
        git {
            remote {
                url('http://john.smith@10.0.3.70/gerrit/p/jenkins/scripts.git')
              credentials('f5aef68c-243d-400f-b43f-c7abfc714309')
               branch("*/master")
            }
               extensions {
                relativeTargetDirectory('$WORKSPACE/jenkins_script')
            
            }
        }
    }
    label('docker')
    steps {
		shell('''#!/bin/bash 
			# cd $WORKSPACE
			#./FMW_Instance_Create.sh $WORKSPACE
			
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
			WORKSPACE=`echo $WORKSPACE/instance_name.json`.strip
			jsonfile = {
				"jenkins_db_instance_name" => [FMW_DB_NAME],
			"jenkins_server_instance_name" => [FMW_SERVER_NAME]
			}
			File.open(WORKSPACE,"w+") do |f|
			f.write(JSON.pretty_generate(jsonfile))
			end 
			''')
 	
    }
    publishers {
			downstreamParameterized {
					trigger('Create_FMW_Environment') {
								condition('SUCCESS')
                      parameters{
                        currentBuild()
						predefinedProp('CUSTOM_WORKSPACE', '$WORKSPACE')
                      }//propertiesFile('$WORKSPACE/build.properties', false)
                      			
					}
			}
      		downstreamParameterized {
					trigger('Create_Database_Environment') {
								condition('SUCCESS')
                      parameters{
                        currentBuild()
						predefinedProp('CUSTOM_WORKSPACE', '$WORKSPACE')
                      }//propertiesFile('$WORKSPACE/build.properties', false)
                      			
					}
			}
    }
  }