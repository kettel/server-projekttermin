#!/bin/bash
ip=$(ifconfig eth0 | awk '/inet addr/ {split ($2,A,":"); print A[2]}')
server="192.168.1.128"

if [ $ip=$server ]
	then 
		port=17234
		jetty=16783
		v=$(echo ../lib/*.jar | tr ' ' ':../')
		javac -cp .:../$v  database/*.java model/*.java server/*.java jetty/*.java
		echo "Kompilerat färdigt. Nu ska servern startas!"
		java -cp .:../$v server.Server $port $jetty
else
	port=18234
	jetty=17783
	v=$(echo ../lib/*.jar | tr ' ' ':../')
	javac -cp .:../$v  database/*.java model/*.java server/*.java jetty/*.java
	echo "Kompilerat färdigt. Nu ska servern startas!"
	java -cp .:../$v -Djava.awt.headless=true server.Server $port $jetty
fi

