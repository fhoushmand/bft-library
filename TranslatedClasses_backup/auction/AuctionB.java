package bftsmart.usecase.auction;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.usecase.Client;
public class AuctionB extends PartitionedObject {
	public AirlineAgentClient B;
	
	public void m3(String callerId, Integer n, Integer offerA, OfferInfo seatInfoB, Integer u){
		logger.trace("execute m3");
		Integer offerB = (Integer) runtime.invokeObj("B", "makeOfferB2", "m3", callerId+"::m3", ++n, u, offerA);
		runtime.invoke("m2", callerId+"::m3", ++n, offerB, offerA, seatInfoB);
	}
	public void m4(String callerId, Integer n, Integer offerA, Integer u){
		logger.trace("execute m4");
		OfferInfo seatInfoB = (OfferInfo) runtime.invokeObj("B", "makeOfferB1", "m4", callerId+"::m4", ++n, u, offerA);
		runtime.invoke("m3", callerId+"::m4", ++n, offerA, seatInfoB, u);
	}
}