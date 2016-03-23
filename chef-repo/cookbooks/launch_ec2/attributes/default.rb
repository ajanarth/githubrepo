#
# Cookbook Name:: launch_ec2
# Attribute:: default
#
# Copyright 2015, Accenture
#
# All rights reserved - Do Not Redistribute
#

#User details
default[:launch_ec2][:user][:name] = 'root'
default[:launch_ec2][:user][:home] = '/root'
default[:launch_ec2][:user][:profile] = '.profile'

#Configuration
default[:launch_ec2][:config][:region] = 'eu-west-1'
default[:launch_ec2][:config][:output] = 'text'
default[:launch_ec2][:config][:home] = '/root/.aws'
default[:launch_ec2][:config][:cfg_file] = :config
default[:launch_ec2][:config][:credential_file] = 'credentials'
default[:launch_ec2][:config][:aws_pem] = '/opt/Software/devtools/chef-repo/aesg_key.pem'
default[:launch_ec2][:config][:chef_repo_loc] = '/opt/Software/devtools/chef-repo'
default[:launch_ec2][:config][:db_instance] = '/tmp/db_id.txt'
default[:launch_ec2][:config][:server_instance] = '/tmp/server_id.txt'


#AWS CLI Create FMW SERVER recipe
default[:launch_ec2][:server][:ami] = 'ami-c46e52ae'
default[:launch_ec2][:server][:type] = 'm4.large'
default[:launch_ec2][:server][:sgi] = 'sg-cc9041b4'
default[:launch_ec2][:server][:count] = '1'
default[:launch_ec2][:server][:keyname] = 'fmw_oracle'
default[:launch_ec2][:server][:availability_zone] = 'us-east-1c'
default[:launch_ec2][:server][:subnetid] = 'subnet-0c7ea826'


#AWS CLI Create FMW DB recipe
default[:launch_ec2][:db][:ami] = 'ami-70d9e41a'
default[:launch_ec2][:db][:type] = 'm3.2xlarge'
default[:launch_ec2][:db][:sgi] = 'sg-ec964894'
default[:launch_ec2][:db][:count] = '1'
default[:launch_ec2][:db][:keyname] = 'fmw_oracle'
default[:launch_ec2][:db][:availability_zone] = 'us-east-1c'
default[:launch_ec2][:db][:subnetid] = 'subnet-0c7ea826'


#AWS CLI Terminate recipe
#default['aesg_aws']['ec2']['instance_id'] = ['i-e1625758', 'i-bc764305', 'i-fb635642', 'i-017247b8', 'i-81665338', 'i-bd645104', 'i-d1655068', 'i-807a4f39', 'i-8d7e4b34', 'i-926d582b']
default[:launch_ec2][:ec2][:terminate] = true