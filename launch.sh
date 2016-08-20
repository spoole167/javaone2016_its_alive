#!/bin/bash
SERIAL=`cat /proc/cpuinfo | grep Serial | cut -d ':' -f 2`
mvn install
cd launcher
export SERIAL 
mvn exec:java
