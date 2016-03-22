#!/bin/bash

JSONFILE_NAME=$1/instance_name.json
sudo rm -rf $JSONFILE_NAME
sudo touch $JSONFILE_NAME
sudo chmod 777 $JSONFILE_NAME
> $JSONFILE_NAME
