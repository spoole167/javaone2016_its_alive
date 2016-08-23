#!/bin/bash
SERIAL=`cat /proc/cpuinfo | grep Serial | cut -d ':' -f 2`
git pull
mvn install
cd launcher
export SERIAL 
mvn exec:java
