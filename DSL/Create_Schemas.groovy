freeStyleJob('Environment_Provisioning/Create_Schemas') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps {
    shell('''#!/bin/bash
			sleep 375
			echo "successful"
		''')
    }
    publishers {
		downstreamParameterized {
			trigger('Create_SOA_Domain') {
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