package bftsmart.usecase.oblivioustransfer;

import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.PartitionedObject;

import java.util.Arrays;
import java.util.HashMap;

public class OTClusterRunner {
    public static void main(String[] args) throws Exception {

        //set of host ip addresses in a distributed environment
        HashMap<Integer,String> hostMap = new HashMap<>();
        int id = 0;
        for(String hostIP : Arrays.copyOfRange(args, 1, args.length))
            hostMap.put(id++, hostIP+".ib.hpcc.ucr.edu");

        PartitionedObject object = new PartitionedObject(hostMap);


        int clusterId = Integer.parseInt(args[0]);
        int i = 0;
        if(clusterId < object.getHosts().get(0).size())
            RMIRuntime.main(new String[]{String.valueOf(clusterId), String.valueOf(1), "bftsmart.usecase.oblivioustransfer.OTA"});

        else if(clusterId >= object.getHosts().get(0).size() && clusterId < object.getHosts().get(0).size() + object.getHosts().get(1).size())
            RMIRuntime.main(new String[]{String.valueOf(clusterId), String.valueOf(2), "bftsmart.usecase.oblivioustransfer.OTB"});

        if(clusterId == object.getHosts().get(0).size() + object.getHosts().get(1).size()) {
            Thread.sleep(10000);
            RMIRuntime.main(new String[]{String.valueOf(clusterId), String.valueOf(3), "bftsmart.usecase.oblivioustransfer.OTClient"});
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
