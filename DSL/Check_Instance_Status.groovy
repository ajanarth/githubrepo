freeStyleJob('Environment_Provisioning/Check_Instance_Status') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps {
    shell('''#!/bin/bash
			#cd $WORKSPACE/jenkins_script
			#sudo ./Check_Instance_Status.sh $WORKSPACE
			
			#run the check_status chef cookbook to check if the instances are running.
			cd chef-repo
			sudo chef-solo -c solo.rb -o launch_ec2::check_status || exit 1
		''')
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