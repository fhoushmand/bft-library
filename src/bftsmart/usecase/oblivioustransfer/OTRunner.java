package bftsmart.usecase.oblivioustransfer;

import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class OTRunner {
    public static void main(String[] args) throws Exception {

        HashMap<Integer,String> hostIPMap = new HashMap<>();
        for (int i = 0; i < 30; i++)
            hostIPMap.put(i, "127.0.0.1");
        PartitionedObject object = new PartitionedObject(hostIPMap);

        int i = 0;
        for(i = 0; i < object.getHosts().get(0).size(); i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    RMIRuntime.main(new String[]{String.valueOf(finalI), String.valueOf(1), "bftsmart.usecase.oblivioustransfer.OTA"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        for(; i < object.getHosts().get(0).size() + object.getHosts().get(1).size(); i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    RMIRuntime.main(new String[]{String.valueOf(finalI), String.valueOf(2), "bftsmart.usecase.oblivioustransfer.OTB"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(10000);
        RMIRuntime.main(new String[]{String.valueOf(i), String.valueOf(3), "bftsmart.usecase.oblivioustransfer.OTClient"});


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
