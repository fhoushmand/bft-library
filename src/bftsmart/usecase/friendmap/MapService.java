package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.MapServiceClient;
import bftsmart.usecase.PartitionedObject;

public class MapService extends PartitionedObject {
    public MapServiceClient mapServ;

    public void m2(String callerId, Integer n, String box)
    {
        String map = (String) runtime.invokeObj("mapServ", "getMap", "m2", callerId+"::m2", ++n, box);
        runtime.invoke("m1", callerId+"::m2", ++n, map);
    }
}
