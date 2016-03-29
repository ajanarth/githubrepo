freeStyleJob('Environment_Provisioning/Requirement_Preparation') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps {
    shell('''#!/bin/bash
			cd jenkins_script
			chmod +x *.*
			./Requirement_Preparation.sh $AWS_PEM $WORKSPACE
		''')
    }
    publishers {
		downstreamParameterized {
			trigger('Install_FMW') {
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