#!/bin/bash
ip=$(ifconfig eth0 | awk '/inet addr/ {split ($2,A,":"); print A[2]}')
server2=`echo $ip | cut -d\. -f4`

if [ $server2 -eq 128 ]
	then 
	port=18234
	jetty=17783
	v=$(echo ../lib/*.jar | tr ' ' ':../')
	javac -cp .:../$v  database/*.java model/*.java server/*.java jetty/*.java
	echo "Kompilerat färdigt. Nu ska servern 2 startas!"
	java -cp .:../$v -Djava.awt.headless=true server.Server $port $jetty
else
	port=17234
	jetty=16783
	v=$(echo ../lib/*.jar | tr ' ' ':../')
	javac -cp .:../$v  database/*.java model/*.java server/*.java jetty/*.java
	echo "Kompilerat färdigt. Nu ska servern 1 startas!"
	java -cp .:../$v -Djava.awt.headless=true server.Server $port $jetty
fi

