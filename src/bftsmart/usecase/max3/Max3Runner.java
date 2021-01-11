package bftsmart.usecase.max3;

import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.PartitionedObject;

public class Max3Runner {
    private static final Object lock = new Object();
    private static int counter = 0;

//    public static void main(String[] _args) {
//        while (true) {
//            new Thread(new Runnable() {
//                public void run() {
//                    synchronized(lock) {
//                        counter++;
//                        System.err.println("New thread #" + counter);
//                    }
//                    while (true) {
//                        try {
//                            Thread.sleep(3000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
//        }
//    }
    public static void main(String[] args) throws Exception {
        PartitionedObject object = new PartitionedObject();
        int i = 0;
        for(; i < object.getHosts().get(0).size(); i++) {
            int finalI = i;
            counter++;
            System.err.println("New thread #" + counter);
            new Thread(() -> {
                try {
                    RMIRuntime.main(new String[]{String.valueOf(finalI), String.valueOf(1), "bftsmart.usecase.max3.Max3A"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        for(; i < object.getHosts().get(0).size() + object.getHosts().get(1).size(); i++) {
            int finalI = i;
            counter++;
            System.err.println("New thread #" + counter);
            new Thread(() -> {
                try {
                    RMIRuntime.main(new String[]{String.valueOf(finalI), String.valueOf(2), "bftsmart.usecase.max3.Max3B"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        for(; i < object.getHosts().get(0).size() + object.getHosts().get(1).size() + object.getHosts().get(2).size(); i++) {
            int finalI = i;
            counter++;
            System.err.println("New thread #" + counter);
            new Thread(() -> {
                try {
                    RMIRuntime.main(new String[]{String.valueOf(finalI), String.valueOf(3), "bftsmart.usecase.max3.Max3C"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(10000);
        RMIRuntime.main(new String[]{String.valueOf(i), String.valueOf(1), "bftsmart.usecase.max3.Max3Client"});


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
