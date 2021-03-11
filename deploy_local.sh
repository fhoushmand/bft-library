#!/bin/bash -l

rm -rf config_*
rm -rf runtimeconfig_*

hostlist=''
local="127.0.0.1 "
for i in $( seq 0 $2 ); do
        hostlist="$local$hostlist"
done

echo $hostlist

for i in $( seq 0 $2 ); do
        sh run_local.sh $1 $i $hostlist &
done

sleep 600
