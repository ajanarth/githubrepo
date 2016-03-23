#
# Cookbook Name:: fmw_aws
# Recipe:: check_status
#
# Copyright 2015, Accenture
#
# All rights reserved - Do Not Redistribute
#

ruby_block "CHECK_EC2_INSTANCE" do
		block do
		ENV['PATH'] = "#{node[:launch_ec2][:user][:home]}/bin:/opt/chefdk/embedded/bin:/root/.chefdk/gem/ruby/2.1.0/bin:/sbin:/bin:/usr/sbin:/usr/bin"
		system("echo PATH = $PATH")
		
		#read the server instance id text file
		#file="#{node['aesg_aws']['config']['instance_list']}"
		#	File.readlines(file).each do |instanceserverid|
			
		instanceserverid=`cat "#{node[:launch_ec2][:config][:server_instance]}" | grep i`
					
			#remove the extra whitespace
			instanceid="#{instanceserverid.strip}"
			
			puts "\n\nINFO: Checking the #{instanceid} instance status..."
				
				#$ECINSTANCESTATUS=`aws ec2 describe-instance-status --instance-id #{instanceid} | grep -w INSTANCESTATUS | awk '{print $2}'`
				instancestatus="initializing"
				#puts "Instance Status is #{instancestatus}"
				#$SYSTEMSTATUS=`aws ec2 describe-instance-status --instance-id #{instanceid} | grep -w SYSTEMSTATUS | awk '{print $2}'`
				instancasystemstatus="initializing"
				#puts "System Status is #{instancasystemstatus}"
				
				while "#{instancestatus}" == "initializing" || "#{instancasystemstatus}" == "initializing" do
					puts "Initializing EC2 instance #{instanceid}"
					sleep 20
					$ECINSTANCESTATUS=`sudo /usr/local/bin/aws ec2 describe-instance-status --instance-id #{instanceid} | grep -w INSTANCESTATUS | awk '{print $2}'`
					instancestatus="#{$ECINSTANCESTATUS.strip}"
						puts "Instance Status: #{instancestatus}"
					$SYSTEMSTATUS=`sudo /usr/local/bin/aws ec2 describe-instance-status --instance-id #{instanceid} | grep -w SYSTEMSTATUS | awk '{print $2}'`
					instancasystemstatus="#{$SYSTEMSTATUS.strip}"
						puts "System Status: #{instancasystemstatus}"
				end
				
				puts "SUCCESS: EC2 instance #{instanceid} initialization complete\n"
	
			
		end

		#remove the instance list text file after use
		#puts "Removing #{node[:launch_ec2][:config][:server_instance]}"
		#puts "Removing #{node[:launch_ec2][:config][:db_instance]}"
		#File.delete("#{node[:launch_ec2][:config][:server_instance]}")
		#File.delete("#{node[:launch_ec2][:config][:db_instance]}")
	
	end



