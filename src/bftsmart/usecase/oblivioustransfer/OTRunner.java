package bftsmart.usecase.oblivioustransfer;

import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.PartitionedObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class OTRunner {
//    public static void main(String[] args) throws Exception {
//        String hosts = "";
//        HashMap<Integer,String> hostIPMap = new HashMap<>();
//        for (int i = 0; i < 9; i++){
//            hostIPMap.put(i, "127.0.0.1");
//            hosts += "127.0.0.1 ";
//        }
//        PartitionedObject object = new PartitionedObject(hostIPMap);
//
//        int i = 0;
//        for(i = 0; i < object.getHosts().get(0).size(); i++) {
//            int finalI = i;
//            String finalHosts1 = hosts;
//            new Thread(() -> {
//                try {
//                    RMIRuntime.main(new String[]{String.valueOf(finalI), String.valueOf(1), "bftsmart.usecase.oblivioustransfer.OTA", finalHosts1});
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//        for(; i < object.getHosts().get(0).size() + object.getHosts().get(1).size(); i++) {
//            int finalI = i;
//            String finalHosts = hosts;
//            new Thread(() -> {
//                try {
//                    RMIRuntime.main(new String[]{String.valueOf(finalI), String.valueOf(2), "bftsmart.usecase.oblivioustransfer.OTB", finalHosts});
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//        Thread.sleep(10000);
//        RMIRuntime.main(new String[]{String.valueOf(i), String.valueOf(3), "bftsmart.usecase.oblivioustransfer.OTClient", hosts});
//
//
//    }

    public static void main(String[] args) throws Exception {

        int totalNumberOfHosts = 0;
        // duplicate??
        try
        {
            FileReader fr = new FileReader("systemconfig/ot.config");
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

        String hosts = "";
        for(int i = 0; i < totalNumberOfHosts; i++) {
            hosts += "127.0.0.1 ";
        }

        try
        {
            FileReader fr = new FileReader("systemconfig/ot.config");
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
                                RMIRuntime.main(new String[]{h, clusterId, partitionedClassName, finalHosts});
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

    public static int recommendedThreadCount()
    {
        int mRtnValue = 0;
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long mTotalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        int mAvailableProcessors = runtime.availableProcessors();

        long mTotalFreeMemory = freeMemory + (maxMemory - mTotalMemory);
        mRtnValue = (int)(mTotalFreeMemory/4200000000l);

        int mNoOfThreads = mAvailableProcessors-1;
        if(mNoOfThreads < mRtnValue) mRtnValue = mNoOfThreads;

        return mRtnValue;
    }
}
