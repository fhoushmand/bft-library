#!/usr/bin -l

#/bigdata/operations/pkgadmin/opt/linux/centos/7.x/x86_64/pkgs/java/jdk1.8.0_45/bin/java
/opt/linux/centos/7.x/x86_64/pkgs/java/jdk-11.0.2/bin/java -Djava.security.properties="./systemconfig/java.security" -Dlogback.configurationFile="./systemconfig/logback.xml" -Xmx20000m -cp bin/*:lib/* bftsmart.usecase.NodeClusterRunner $@ > $2.log