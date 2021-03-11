#!/usr/bin -l

java -Djava.security.properties="./systemconfig/java.security" -Dlogback.configurationFile="./systemconfig/logback.xml" -Xmx10000m -cp bin/*:lib/* bftsmart.usecase.NodeClusterRunner $@ > $2.log