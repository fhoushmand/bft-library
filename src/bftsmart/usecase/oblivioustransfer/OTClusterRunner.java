package bftsmart.usecase.oblivioustransfer;

import bftsmart.runtime.CMDReader;
import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.PartitionedObject;
import bftsmart.usecase.max3.Max3Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

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

    /*
    * @param args[0] is the config file in systemconfig
    * @param args[1] is the id of the current runtime
    * @param args[2:n] is the host list name
     */
    public static void main(String[] args) throws Exception {
        OTClusterRunner clusterRunner = new OTClusterRunner(args[0]);

//        RMIRuntime.main(new String[]{args[1], clusterRunner.config.get(Integer.parseInt(args[1])).cluster, clusterRunner.config.get(Integer.parseInt(args[1])).className, hosts, args[0]});


        RMIRuntime.CONFIGURATION = args[0].split("/")[args[0].split("/").length-1];
        int id = Integer.parseInt(args[1]);
        // cluster id responsible for replicating the piece of
        // information in the partitioned object. for OTA it is 1
        // and for OTB it is 3
        //TODO need to make it general for other partitioned objects with multiple object fields
        int clusterId = Integer.parseInt(clusterRunner.config.get(Integer.parseInt(args[1])).cluster);

        HashMap<Integer,String> hostIPMap = new HashMap<>();
        int i = 0;
        for (String hostName : Arrays.copyOfRange(args, 2, args.length)){
            String h = hostName + ".ib.hpcc.ucr.edu";
            hostIPMap.put(i++, h);
        }

        PartitionedObject o = (PartitionedObject) Class.forName(clusterRunner.config.get(Integer.parseInt(args[1])).className).getConstructor(HashMap.class, String.class).newInstance(hostIPMap, RMIRuntime.CONFIGURATION);

        RMIRuntime runtime = new RMIRuntime(id, clusterId, o);
        runtime.getObj().setRuntime(runtime);
        runtime.start();

        //read from the queue (randomly generating inputs)
        if (runtime.getObj() instanceof Max3Client || runtime.getObj() instanceof OTClient)
        {
            CMDReader reader = new CMDReader();
            runtime.setInputReader(reader);
            reader.runtime = runtime;
        }

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
