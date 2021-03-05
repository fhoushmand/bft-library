package bftsmart.usecase.auction;

import bftsmart.runtime.CMDReader;
import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.Client;
import bftsmart.usecase.Configuration;
import bftsmart.usecase.PartitionedObject;
import bftsmart.usecase.Spec;
import bftsmart.usecase.oblivioustransfer.OTClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class AuctionRunner {
    public static void main(String[] args) throws Exception {

        int totalNumberOfHosts = 0;
        // duplicate??
        try
        {
            FileReader fr = new FileReader(args[0]);
            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            while ((line = rd.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String hostList = line.split("\\s+")[2];
                    for(String h : hostList.split(","))
                        totalNumberOfHosts++;
                }
            }
            fr.close();
            rd.close();
        }
        catch (IOException e)
        {
            System.out.println("Cannot read use-case config file");
        }
        HashMap<Integer,String> hostIPMap = new HashMap<>();
        String hosts = "";
        for(int i = 0; i < totalNumberOfHosts; i++) {
            hosts += "127.0.0.1 ";
            hostIPMap.put(i, "127.0.0.1");
        }

        try
        {
            FileReader fr = new FileReader(args[0]);
            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            Spec spec = new Spec(args[0], hostIPMap);

            for(Configuration config : spec.getConfigurations().values()) {
                for(Integer host : config.getHostSet())
                new Thread(() -> {
                    try {
                        RMIRuntime.CONFIGURATION = args[0].split("/")[args[0].split("/").length-1];

                        PartitionedObject o = (PartitionedObject) Class.forName(config.getClassName()).getConstructor().newInstance();

                        RMIRuntime runtime = new RMIRuntime(host, Integer.parseInt(config.getCluster()), spec, o);
                        runtime.start();

                        //read from the queue (randomly generating inputs)
                        if (runtime.getObj() instanceof Client)
                        {
                            CMDReader reader = new CMDReader();
                            runtime.setInputReader(reader);
                            reader.runtime = runtime;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }


//            while ((line = rd.readLine()) != null) {
//                if (!line.startsWith("#")) {
//                    String hostsSetName = line.split("\\s+")[0];
//                    String partitionedClassName = line.split("\\s+")[1];
//                    String hostList = line.split("\\s+")[2];
//                    String clusterId = line.split("\\s+")[3];
//                    if(hostsSetName.equals("Client"))
//                        Thread.sleep(10000);
//                    for(String h : hostList.split(",")) {
//                        new Thread(() -> {
//                            try {
//                                RMIRuntime.CONFIGURATION = args[0].split("/")[args[0].split("/").length-1];
//
//                                PartitionedObject o = (PartitionedObject) Class.forName(partitionedClassName).getConstructor().newInstance();
//
//                                RMIRuntime runtime = new RMIRuntime(Integer.parseInt(h), Integer.parseInt(clusterId), spec);
////                                runtime.getObj().setRuntime(runtime);
//                                runtime.start();
//
//                                //read from the queue (randomly generating inputs)
//                                if (runtime.getObj() instanceof Client)
//                                {
//                                    CMDReader reader = new CMDReader();
//                                    runtime.setInputReader(reader);
//                                    reader.runtime = runtime;
//
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }).start();
//                    }
//                }
//            }
            fr.close();
            rd.close();
        }
        catch (IOException e)
        {
            System.out.println("Cannot read use-case config file");
        }
    }
}
