freeStyleJob('Create_Environment_1/Smoke_Test') {
	customWorkspace('$CUSTOM_WORKSPACE')
    logRotator(-1, 10)
    label('docker')
    steps { 
    shell('''#!/bin/bash
			SERVER_ID="/tmp/server_id.txt"
			DB_ID="/tmp/db_id.txt"
			JSONFILE=$2/instance_ip.json

			srvrinstid=$(cat $SERVER_ID)
			SERVER_INSTANCEIP=`sudo aws ec2 describe-instances --instance-ids $srvrinstid | grep PRIVATEIPADDRESSES |  awk '{print $4}'`
			echo "SERVER IP is: $SERVER_INSTANCEIP"

			echo "BASEURL=http://$SERVER_INSTANCEIP:7001/console" > /opt/selenium/fmwConfig.properties
			echo "USER=Uname" >> /opt/selenium/fmwConfig.properties
			echo "PASSWORD=Pword" >> /opt/selenium/fmwConfig.properties
			echo "NODEURL=http://selenium-hub:4444/wd/hub" >> /opt/selenium/fmwConfig.properties
			echo "REPORTLOCATION=/workspace/Create_Environment/Set_Provisioning_Parameters/" >> /opt/selenium/fmwConfig.properties
			echo "IMAGELOCATION=/workspace/Create_Environment/Set_Provisioning_Parameters/" >> /opt/selenium/fmwConfig.properties
			echo "IMAGEMAP=http://10.0.3.70/jenkins/job/Create_Environment/job/Smoke_Test/ws/" >> /opt/selenium/fmwConfig.properties

			cd /opt/scripts
			sudo java -jar FMW_test.jar 

			#sudo rm -rf /tmp/server_id.txt /tmp/db_id.txt

		''')
    }
    publishers {
		publishHtml {
            report('$WORKSPACE') {
                reportName('Report.html')
            }
            report('test') {
                reportName('HTML Report')
            }
        }
    }
}