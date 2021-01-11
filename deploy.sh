#!/bin/bash -l

#SBATCH --nodes=9
#SBATCH --ntasks=9
#SBATCH --cpus-per-task=4
#SBATCH --output="ot.log"
#SBATCH --mem=5G
#SBATCH -p short # This is the default partition, you can use any of the following; intel, batch, highmem, gpu

module load java/11

rm -rf bin/*
#rm config/currentView*
#rm myconfig/currentView*
#

##mv cluster-config/* .
#java -XshowSettings 2>&1  | grep -i Heap
#jps -lvm
#ps -o nlwp,pid -fe
#~/shared/opt/apache-ant-1.10.9/bin/ant -d main

nodes=($( scontrol show hostnames $SLURM_NODELIST ))
nnodes=${#nodes[@]}
last=$(( $nnodes - 1 ))

hostlist=""
basehostaddress=".ib.hpcc.ucr.edu"

for i in $( seq 0 $last ); do
        hostlist+="${nodes[$i]} "
done

echo $hostlist

export HAMRAZ_HOME=/rhome/fhous001/shared/hamraz

for i in $( seq 0 $last ); do
        ssh ${nodes[$i]}.ib.hpcc.ucr.edu "cd ${HAMRAZ_HOME}; module load java/11; run.sh $i $hostlist" &
done

# Move to directory where job was submitted from
cd $SLURM_WORKING_DIR


#for a in 0 1 2 3 4 5 6 7 8 9 10 11 12
#do
#        sh runscripts/smartrun.sh bftsmart.runtime.RMIRuntime $a 1 bftsmart.usecase.oblivioustransfer.OTA&
#done
#
#for b in 13 14 15 16 17 18 19 20 21 22 23 24 25
#do
#        sh runscripts/smartrun.sh bftsmart.runtime.RMIRuntime $b 2 bftsmart.usecase.oblivioustransfer.OTB&
#done
#
#sh runscripts/smartrun.sh bftsmart.runtime.RMIRuntime 26 3 bftsmart.usecase.oblivioustransfer.OTClient

#java -Djava.security.properties="./systemconfig/java.security" -Dlogback.configurationFile="./systemconfig/logback.xml" -Xmx20000m -cp bin/*:lib/* bftsmart.usecase.oblivioustransfer.OTRunner