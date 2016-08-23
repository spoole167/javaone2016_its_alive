#!/bin/bash

SERIAL=`cat /proc/cpuinfo | grep Serial | cut -d ':' -f 2`
export SERIAL

while : ; do
    echo "launcher starting $SERIAL"
	git pull

	cd launcher

	mvn clean compile assembly:single
	 
	java -jar target/launcher-0.0.1-SNAPSHOT-jar-with-dependencies.jar
	
	if [[ $? -ne 50 ]]; then
		break;
	fi

done


