package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.SnappClient;
import bftsmart.usecase.PartitionedObject;

public class SnappService extends PartitionedObject {
    public SnappClient Snapp;

    public void m8(String callerId, Integer n)
    {
        //System.out.println("execute m8");
        String box = (String) runtime.invokeObj("Alice", "newBox", "m8", callerId+"::m8", ++n);
        runtime.invoke("m7", callerId+"::m8", ++n, box);
    }

    public void m7(String callerId, Integer n, String box)
    {
        int aID = (int) runtime.invokeObj("Alice", "getID", "m7", callerId+"::m7", ++n);
        runtime.invoke("m6", callerId+"::m6", ++n, box, aID);
    }

    public void m6(String callerId, Integer n, String box, Integer aID)
    {
        int bID = (int) runtime.invokeObj("Bob", "getID", "m6", callerId+"::m6", ++n);
        runtime.invoke("m5", callerId+"::m6", ++n, box, aID, bID);
    }

    public void m5(String callerId, Integer n, String box, Integer aID, Integer bID)
    {
        boolean isFriend = (boolean) runtime.invokeObj("Snapp", "isFriend", "m5", callerId+"::m5", ++n, aID, bID);
        if(isFriend)
            runtime.invoke("m4", callerId+"::m5", ++n, box);
        else
            runtime.invoke("ret", callerId+"::m5", ++n, box);
    }
}
