#!/bin/bash
v=$(echo ../lib/*.jar | tr ' ' ':../')
javac -cp .:../$v  database/*.java model/*.java server/*.java jetty/*.java
java -cp .:../$v  server.Server
