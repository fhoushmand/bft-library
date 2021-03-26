module load java/11;
mkdir -p bin;
rm -rf bin/*;
rm -rf config_*;
rm -rf runtimeconfig_*;
srun -p short --mem 50gb --cpus-per-task 64 --ntasks 1 --pty ~/shared/opt/apache-ant-1.10.9/bin/ant -d main