#!/bin/bash -l

#SBATCH --nodes=1
#SBATCH --ntasks=32
#SBATCH --cpus-per-task=2
#SBATCH --output="ot.log"
#SBATCH --mem=20G
#SBATCH -p short # This is the default partition, you can use any of the following; intel, batch, highmem, gpu

module load java/11



rm -rf bin/*
rm config/currentView*
rm myconfig/currentView*

#rm -rf config
#rm -rf myconfig
#mv cluster-config/* .
~/shared/opt/apache-ant-1.10.9/bin/ant -d main


for a in {0..6}
do
        sh runscripts/smartrun.sh bftsmart.runtime.RMIRuntime $a 1 bftsmart.usecase.oblivioustransfer.OTA&
done

for b in {7..9}
do
        sh runscripts/smartrun.sh bftsmart.runtime.RMIRuntime $b 2 bftsmart.usecase.oblivioustransfer.OTB&
done

sh runscripts/smartrun.sh bftsmart.runtime.RMIRuntime 10 2 bftsmart.usecase.oblivioustransfer.OTB;

sh runscripts/smartrun.sh bftsmart.runtime.RMIRuntime 11 3 bftsmart.usecase.oblivioustransfer.OTClient