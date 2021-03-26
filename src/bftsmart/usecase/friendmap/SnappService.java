package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.SnappClient;
import bftsmart.usecase.PartitionedObject;

public class SnappService extends PartitionedObject {
    public SnappClient snapp;

    public void m4(String callerId, Integer n, String box, Integer aID, Integer bID)
    {
        boolean isFriend = (boolean) runtime.invokeObj("snapp", "isFriend", "m4", callerId+"::m4", ++n, aID, bID);
        if(isFriend)
            runtime.invoke("m5", callerId+"::m4", ++n, box);
        else
            runtime.invoke("ret", callerId+"::m4", ++n, box);
    }
}
