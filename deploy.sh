#!/bin/bash -l

#SBATCH --nodes=1
#SBATCH --ntasks=32
#SBATCH --cpus-per-task=2
#SBATCH --output="ot.log"
#SBATCH --mem=20G
#SBATCH -p short # This is the default partition, you can use any of the following; intel, batch, highmem, gpu

#module load java/11
#
#
#
#rm -rf bin/*
#rm config/currentView*
#rm myconfig/currentView*
#
##rm -rf config
##rm -rf myconfig
##mv cluster-config/* .
#~/shared/opt/apache-ant-1.10.9/bin/ant -d main


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

java -Djava.security.properties="./systemconfig/java.security" -Dlogback.configurationFile="./systemconfig/logback.xml" -Xmx20000m -cp bin/*:lib/* bftsmart.usecase.oblivioustransfer.UseCaseRunner