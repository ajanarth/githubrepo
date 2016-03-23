freeStyleJob('Environment_Provisioning/Create_Database_Environment') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10) 
    label('docker')
    steps {
    shell('''#!/bin/bash
				sleep 38
				echo "successful"
				
		''')
    }
	
}