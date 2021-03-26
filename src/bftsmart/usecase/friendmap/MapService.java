package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.MapServiceClient;
import bftsmart.usecase.PartitionedObject;

public class MapService extends PartitionedObject {
    public MapServiceClient mapService;

    public void m7(String callerId, Integer n, String box)
    {
        String map = (String) runtime.invokeObj("mapService", "getMap", "m7", callerId+"::m7", ++n, box);
        runtime.invoke("m8", callerId+"::m7", ++n, map);
    }
}
