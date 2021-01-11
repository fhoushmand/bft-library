package bftsmart.usecase.oblivioustransfer;

import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.PartitionedObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

public class OTClusterRunner {

    HashMap<Integer,Config> config = new HashMap<>();

    public OTClusterRunner(String configPath) {
        readUseCaseConfig(configPath);
    }

    public void readUseCaseConfig(String configpath)
    {
        try
        {
            FileReader fr = new FileReader(configpath);
            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            while ((line = rd.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String hostsSetName = line.split("\\s+")[0];
                    String partitionedClassName = line.split("\\s+")[1];
                    String hostList = line.split("\\s+")[2];
                    String clusterId = line.split("\\s+")[3];
                    for(String h : hostList.split(","))
                        config.put(Integer.parseInt(h), new Config(hostsSetName, partitionedClassName, clusterId));
                }
            }
            fr.close();
            rd.close();
        }
        catch (IOException e)
        {
            System.out.println("Cannot read use-case config file");
        }

    }

    public static void main(String[] args) throws Exception {
        OTClusterRunner clusterRunner = new OTClusterRunner("systemconfig/ot.config");
        int id = 0;
        String hosts = "";
        for(String hostIP : Arrays.copyOfRange(args, 1, args.length)) {
            String h = hostIP + ".ib.hpcc.ucr.edu";
            hosts += h + " ";
        }
        RMIRuntime.main(new String[]{args[0], clusterRunner.config.get(Integer.parseInt(args[0])).cluster, clusterRunner.config.get(Integer.parseInt(args[0])).className, hosts});
//        int clusterId = Integer.parseInt(args[0]);
//        int i = 0;
//        if(clusterId < object.getHosts().get(0).size())
//            RMIRuntime.main(new String[]{String.valueOf(clusterId), String.valueOf(1), "bftsmart.usecase.oblivioustransfer.OTA", hosts});
//
//        else if(clusterId >= object.getHosts().get(0).size() && clusterId < object.getHosts().get(0).size() + object.getHosts().get(1).size())
//            RMIRuntime.main(new String[]{String.valueOf(clusterId), String.valueOf(2), "bftsmart.usecase.oblivioustransfer.OTB", hosts});
//
//        if(clusterId == object.getHosts().get(0).size() + object.getHosts().get(1).size()) {
//            Thread.sleep(10000);
//            RMIRuntime.main(new String[]{String.valueOf(clusterId), String.valueOf(3), "bftsmart.usecase.oblivioustransfer.OTClient", hosts});
//        }

    }
}

class Config{
    String name;
    String className;
    String cluster;

    public Config(String name, String className, String clusterId) {
        this.name = name;
        this.className = className;
        this.cluster = clusterId;
    }
}
