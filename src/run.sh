#!/bin/bash
v=$(echo ../lib/*.jar | tr ' ' ':../')
javac -cp .:../$v  database/*.java model/*.java server/*.java jetty/*.java
echo "Kompilerat färdigt. Nu ska servern startas!"
java -cp .:../$v  server.Server
