#!/bin/bash -l

rm -rf bin/*
~/shared/opt/apache-ant-1.10.9/bin/ant -d main

java -Djava.security.properties="./systemconfig/java.security" -Dlogback.configurationFile="./systemconfig/logback.xml" -Xmx20000m -cp bin/*:lib/* bftsmart.usecase.oblivioustransfer.OTClusterRunner $@