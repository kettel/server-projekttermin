#!/bin/bash
if [ "$1" -eq 1 ]
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

