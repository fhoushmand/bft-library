package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.AliceClient;
import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

public class UserA extends PartitionedObject{
    public AliceClient Alice;

    public void m4(String callerId, Integer n, String box)
    {
        String location = (String) runtime.invokeObj("Bob", "location", "m4", callerId+"::m4", ++n);
        runtime.invoke("m3", callerId+"::m4", ++n, box, location);
    }

    public void m3(String callerId, Integer n, String box, String loc)
    {
        String newBox = (String) runtime.invokeObj("Alice", "expand", "m3", callerId+"::m3", ++n, box, loc);
        runtime.invoke("m2", callerId+"::m3", ++n, newBox);
    }

    public void m1(String callerId, Integer n, String map)
    {
        String bComment = (String) runtime.invokeObj("Bob", "comment", "m1", callerId+"::m1", ++n);
        runtime.invoke("m0", callerId+"::m1", ++n, map, bComment);
    }

    public void m0(String callerId, Integer n, String map, String bComment)
    {
        String pinnedMap = (String) runtime.invokeObj("Alice", "pin", "m0", callerId+"::m0", ++n, map, bComment);
        runtime.invoke("ret", callerId+"::m0", ++n, pinnedMap);
    }
}
