package bftsmart.usecase.ticket;

import bftsmart.runtime.CMDReader;
import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.Client;
import bftsmart.usecase.Config;
import bftsmart.usecase.PartitionedObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TicketRunner {
    HashMap<Integer, Config> config = new HashMap<>();
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
            while ((line = rd.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String hostsSetName = line.split("\\s+")[0];
                    String partitionedClassName = line.split("\\s+")[1];
                    String hostList = line.split("\\s+")[2];
                    String clusterId = line.split("\\s+")[3];
                    if(hostsSetName.equals("Client"))
                        Thread.sleep(10000);
                    for(String h : hostList.split(",")) {
                        String finalHosts = hosts;
                        new Thread(() -> {
                            try {
//                                RMIRuntime.CONFIGURATION = args[0].substring(args[0].indexOf('('), args[0].indexOf(')')+1);
                                RMIRuntime.CONFIGURATION = args[0].split("/")[args[0].split("/").length-1];

                                PartitionedObject o = (PartitionedObject) Class.forName(partitionedClassName).getConstructor(HashMap.class, String.class).newInstance(hostIPMap, RMIRuntime.CONFIGURATION);

                                RMIRuntime runtime = new RMIRuntime(Integer.parseInt(h), Integer.parseInt(clusterId), o);
                                runtime.getObj().setRuntime(runtime);
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
}
