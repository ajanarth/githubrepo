freeStyleJob('Environment_Provisioning/Create_SOA_Domain') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps {
    shell('''#!/bin/bash
			#cd $WORKSPACE/jenkins_script
			#sudo ./Create_SOA_Domain.sh $AWS_PEM

			SERVER_ID="/tmp/server_id.txt"
			srvrinstid=$(cat $SERVER_ID)
			SERVER_INSTANCEIP=`sudo aws ec2 describe-instances --instance-ids $srvrinstid | grep PRIVATEIPADDRESSES |  awk '{print $4}'`
			echo "SERVER IP is: $SERVER_INSTANCEIP"

			ssh -tt ec2-user@${SERVER_INSTANCEIP} -o StrictHostKeyChecking=no -i $AWS_PEM 'sudo su -c "cd /etc/chef/chef-repo && chef-solo -c solo.rb -o oracle_fmw::create_soa_domain" root'

			ssh -tt ec2-user@${SERVER_INSTANCEIP} -o StrictHostKeyChecking=no -i $AWS_PEM 'sudo su -c "cd /etc/chef/chef-repo && chef-solo -c solo.rb -o oracle_fmw::extend_soa_domain" root'

			ssh -tt ec2-user@${SERVER_INSTANCEIP} -o StrictHostKeyChecking=no -i $AWS_PEM 'sudo su -c "cd /etc/chef/chef-repo && chef-solo -c solo.rb -o oracle_fmw::wlst" root'

			#rm -rf /tmp/server_id.txt /tmp/db_id.txt

		''')
    }
    publishers {
		downstreamParameterized {
			trigger('Smoke_Test') {
				condition('SUCCESS')
                   parameters{
                       currentBuild()
						predefinedProp('CUSTOM_WORKSPACE', '$WORKSPACE')					   
                   }//propertiesFile('$WORKSPACE/build.properties', false)                      			
			}
		}
    }
}