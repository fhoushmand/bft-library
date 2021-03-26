package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.AliceClient;
import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

public class UserA extends PartitionedObject{
    public AliceClient alice;

    public void m1(String callerId, Integer n)
    {
        String box = (String) runtime.invokeObj("alice", "newBox", "m1", callerId+"::m1", ++n);
        int aID = (int) runtime.invokeObj("alice", "getID", "m1", callerId+"::m1", ++n);
        runtime.invoke("m3", callerId+"::m1", ++n, box, aID);
    }

    public void m6(String callerId, Integer n, String box, String loc)
    {
        String newBox = (String) runtime.invokeObj("alice", "expand", "m6", callerId+"::m6", ++n, box, loc);
        runtime.invoke("m7", callerId+"::m6", ++n, newBox);
    }

    public void m9(String callerId, Integer n, String map, String bComment)
    {
        String pinnedMap = (String) runtime.invokeObj("alice", "pin", "m9", callerId+"::m9", ++n, map, bComment);
        runtime.invoke("ret", callerId+"::m9", ++n, pinnedMap);
    }
}
