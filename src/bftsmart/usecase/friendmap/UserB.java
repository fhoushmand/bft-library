package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.BobClient;
import bftsmart.usecase.PartitionedObject;

public class UserB extends PartitionedObject {
    public BobClient bob;

    public void m3(String callerId, Integer n, String box, Integer aID)
    {
        int bID = (int) runtime.invokeObj("bob", "getID", "m3", callerId+"::m3", ++n);
        runtime.invoke("m4", callerId+"::m3", ++n, box, aID, bID);
    }

    public void m5(String callerId, Integer n, String box)
    {
        String location = (String) runtime.invokeObj("bob", "location", "m5", callerId+"::m5", ++n);
        runtime.invoke("m6", callerId+"::m5", ++n, box, location);
    }

    public void m8(String callerId, Integer n, String map)
    {
        String bComment = (String) runtime.invokeObj("bob", "comment", "m8", callerId+"::m8", ++n);
        runtime.invoke("m9", callerId+"::m8", ++n, map, bComment);
    }
}
