freeStyleJob('Environment_Provisioning/Smoke_Test') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps { 
    shell('''#!/bin/bash
			sleep 196
			echo "successful"

		''')
    }
}