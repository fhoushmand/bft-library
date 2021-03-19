
java -Djdk.sunec.disableNative=false -Djava.security.properties="systemconfig\java.security" -Dlogback.configurationFile="systemconfig\logback.xml" -Xmx700m -cp bin\*;lib\* %*
