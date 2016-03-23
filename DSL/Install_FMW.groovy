freeStyleJob('Environment_Provisioning/Install_FMW') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps {
    shell('''#!/bin/bash
			#cd $WORKSPACE/jenkins_script
			#sudo ./Install_FMW.sh $AWS_PEM

			#install the FMW to FMW server
			SERVER_ID="/tmp/server_id.txt"
			srvrinstid=$(cat $SERVER_ID)
			SERVER_INSTANCEIP=`sudo aws ec2 describe-instances --instance-ids $srvrinstid | grep PRIVATEIPADDRESSES |  awk '{print $4}'`
			echo "SERVER IP is: $SERVER_INSTANCEIP"
			ssh -tt ec2-user@${SERVER_INSTANCEIP} -o StrictHostKeyChecking=no -i $AWS_PEM 'sudo su -c "cd /etc/chef/chef-repo && chef-solo -c solo.rb -o oracle_fmw::install" root'
		''')
    }
    publishers {
		downstreamParameterized {
			trigger('Create_Schemas') {
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