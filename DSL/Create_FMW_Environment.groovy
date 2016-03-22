freeStyleJob('Create_Environment_1/Create_FMW_Environment') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps {
    shell('''#!/bin/bash
				#cd $WORKSPACE/jenkins_script
				#sudo ./FMW_SERVER_CREATE.sh $WORKSPACE

				## Run chef cookbook to create a blank aws instance to be used as FMW server.
				cd $WORKSPACE/chef-repo
				chef-solo -c solo.rb -o launch_ec2::fmw_createserver -j $WORKSPACE/instance_name.json || exit 1
		''')
    }
    publishers {
		downstreamParameterized {
			trigger('Check_Instance_Status') {
				condition('SUCCESS')
                   parameters{
                       currentBuild()
						predefinedProp('CUSTOM_WORKSPACE', '$WORKSPACE')					   
                   }//propertiesFile('$WORKSPACE/build.properties', false)                      			
			}
		}
    }
}