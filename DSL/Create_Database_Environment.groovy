freeStyleJob('Create_Environment_1/Create_Database_Environment') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10) 
    label('docker')
    steps {
    shell('''#!/bin/bash
				#cd $WORKSPACE/jenkins_script
				#sudo ./FMW_DB_CREATE.sh $WORKSPACE

				#Create Oracle database to be used in FMW installation
				cd $WORKSPACE/chef-repo
				sudo  chef-solo -c solo.rb -o launch_ec2::fmw_createdb -j $WORKSPACE/instance_name.json || exit 1
		''')
    }
	
}