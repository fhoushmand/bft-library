package bftsmart.usecase.friendmap;

import bftsmart.demo.friendmap.AliceClient;
import bftsmart.demo.friendmap.BobClient;
import bftsmart.usecase.Client;
import bftsmart.demo.friendmap.SnappClient;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.friendmap.MapServiceClient;
public class SnappService extends PartitionedObject {
	public SnappClient Snapp;
	
	public void m5(String callerId, Integer n, String b, Integer bID, Integer aID){
		logger.trace("execute m5");
		Boolean x11 = (Boolean) runtime.invokeObj("Snapp", "isFriend", "m5", callerId+"::m5", ++n, aID, bID);
		if(x11){
			runtime.invoke("m4", callerId+"::m5", ++n, b);
		}
		else{
			runtime.invoke("ret", callerId+"::m5", ++n, b);
		}
	}
	public void m6(String callerId, Integer n, String b, Integer aID){
		logger.trace("execute m6");
		Integer bID = (Integer) runtime.invokeObj("Bob", "ID", "m6", callerId+"::m6", ++n);
		runtime.invoke("m5", callerId+"::m6", ++n, b, bID, aID);
	}
	public void m7(String callerId, Integer n, String b){
		logger.trace("execute m7");
		Integer aID = (Integer) runtime.invokeObj("Alice", "ID", "m7", callerId+"::m7", ++n);
		runtime.invoke("m6", callerId+"::m7", ++n, b, aID);
	}
	public void m8(String callerId, Integer n){
		logger.trace("execute m8");
		String b = (String) runtime.invokeObj("Alice", "newBox", "m8", callerId+"::m8", ++n);
		runtime.invoke("m7", callerId+"::m8", ++n, b);
	}
}