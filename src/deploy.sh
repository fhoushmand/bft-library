rm -rf bin/*
rm -rf config
rm -rf myconfig
mv cluster-config/* .
~/shared/opt/apache-ant-1.10.9/bin/ant -d main