#!/bin/bash -l

#SBATCH --nodes=17
#SBATCH --ntasks=17
#SBATCH --cpus-per-task=4
#SBATCH --output="result.log"
#SBATCH --mem=8500M
#SBATCH -p short # This is the default partition, you can use any of the following; intel, batch, highmem, gpu


module load java/11


mkdir -p bin
rm -rf bin/*
rm -rf config_*
rm -rf runtimeconfig_*


~/shared/opt/apache-ant-1.10.9/bin/ant -d main

nodes=($( scontrol show hostnames $SLURM_NODELIST ))
nnodes=${#nodes[@]}
last=$(( $nnodes - 1 ))

hostlist=""
basehostaddress=".ib.hpcc.ucr.edu"

for i in $( seq 0 $last ); do
        hostlist+="${nodes[$i]} "
done

echo $hostlist

export HAMRAZ_HOME=/rhome/fhous001/shared/bft-library

echo 'ucr2018' | kinit fhous001@HPCC.UCR.EDU

for i in $( seq 0 $last ); do
        printf "ssh ${nodes[$i]}.ib.hpcc.ucr.edu 'cd ${HAMRAZ_HOME};sh run.sh '$1' $i $hostlist'\n"
        ssh ${nodes[$i]}.ib.hpcc.ucr.edu "cd ${HAMRAZ_HOME}; sh run.sh '$1' $i $hostlist" &
done

sleep 450

