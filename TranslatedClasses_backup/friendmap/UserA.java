package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.AliceClient;
import bftsmart.demo.friendmap.BobClient;
import bftsmart.usecase.Client;
import bftsmart.demo.friendmap.SnappClient;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.friendmap.MapServiceClient;
public class UserA extends PartitionedObject {
	public AliceClient Alice;
	
	public void m0(String callerId, Integer n, String bc, String m){
		logger.trace("execute m0");
		String x5 = (String) runtime.invokeObj("Alice", "addComment", "m0", callerId+"::m0", ++n, m, bc);
		runtime.invoke("ret", callerId+"::m0", ++n, x5);
	}
	public void m1(String callerId, Integer n, String m){
		logger.trace("execute m1");
		String bc = (String) runtime.invokeObj("Bob", "comment", "m1", callerId+"::m1", ++n);
		runtime.invoke("m0", callerId+"::m1", ++n, bc, m);
	}
	public void m3(String callerId, Integer n, String b, String bLoc){
		logger.trace("execute m3");
		String newB = (String) runtime.invokeObj("Alice", "expand", "m3", callerId+"::m3", ++n, b, bLoc);
		runtime.invoke("m2", callerId+"::m3", ++n, newB);
	}
	public void m4(String callerId, Integer n, String b){
		logger.trace("execute m4");
		String bLoc = (String) runtime.invokeObj("Bob", "location", "m4", callerId+"::m4", ++n);
		runtime.invoke("m3", callerId+"::m4", ++n, b, bLoc);
	}
}