package bftsmart.usecase.auction;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.usecase.Client;
public class AuctionA extends PartitionedObject {
	public AirlineAgentClient A;
	
	public void m6(String callerId, Integer n, Integer u, OfferInfo seatInfoA, Integer o){
		logger.trace("execute m6");
		Integer offerA = (Integer) runtime.invokeObj("A", "makeOfferA2", "m6", callerId+"::m6", ++n, u, o);
		runtime.invoke("m5", callerId+"::m6", ++n, offerA, u, seatInfoA, o);
	}
	public void m7(String callerId, Integer n, Integer u, Integer o){
		logger.trace("execute m7");
		OfferInfo seatInfoA = (OfferInfo) runtime.invokeObj("A", "makeOfferA1", "m7", callerId+"::m7", ++n, u, o);
		runtime.invoke("m6", callerId+"::m7", ++n, u, seatInfoA, o);
	}
	public void m8(String callerId, Integer n, Integer o){
		logger.trace("execute m8");
		Integer u = (Integer) runtime.invokeObj("user", "read", "m8", callerId+"::m8", ++n);
		runtime.invoke("m7", callerId+"::m8", ++n, u, o);
	}
}