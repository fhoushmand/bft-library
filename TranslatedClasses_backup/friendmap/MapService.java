package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.AliceClient;
import bftsmart.demo.friendmap.BobClient;
import bftsmart.usecase.Client;
import bftsmart.demo.friendmap.SnappClient;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.friendmap.MapServiceClient;
public class MapService extends PartitionedObject {
	public MapServiceClient mapServ;
	
	public void m2(String callerId, Integer n, String newB){
		logger.trace("execute m2");
		String m = (String) runtime.invokeObj("mapServ", "getMap", "m2", callerId+"::m2", ++n, newB);
		runtime.invoke("m1", callerId+"::m2", ++n, m);
	}
}