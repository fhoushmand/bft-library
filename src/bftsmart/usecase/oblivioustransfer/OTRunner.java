package bftsmart.usecase.oblivioustransfer;

import bftsmart.runtime.RMIRuntime;
import bftsmart.usecase.PartitionedObject;

public class OTRunner {
    public static void main(String[] args) throws Exception {
        PartitionedObject object = new PartitionedObject();
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
}
