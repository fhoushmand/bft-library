package bftsmart.usecase.auction;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.usecase.Client;
public class AuctionC extends PartitionedObject {
	public UserAgentClient user;
	
	public void m0(String callerId, Integer n, Integer o){
		logger.trace("execute m0");
		OfferInfo x5 = (OfferInfo) runtime.invokeObj("user", "declareWinner", "m0", callerId+"::m0", ++n, o);
		runtime.invoke("ret", callerId+"::m0", ++n, x5);
	}
	public void m1(String callerId, Integer n, Integer offerA){
		logger.trace("execute m1");
		OfferInfo x13 = (OfferInfo) runtime.invokeObj("user", "declareWinner", "m1", callerId+"::m1", ++n, offerA);
		runtime.invoke("ret", callerId+"::m1", ++n, x13);
	}
	public void m2(String callerId, Integer n, Integer offerB, Integer offerA, OfferInfo seatInfoB){
		logger.trace("execute m2");
		runtime.invokeObj("user", "update", "m2", callerId+"::m2", ++n, seatInfoB, offerB);
		if(offerA >= offerB){
			runtime.invoke("m8", callerId+"::m2", ++n, offerB);
		}
		else{
			runtime.invoke("m1", callerId+"::m2", ++n, offerA);
		}
	}
	public void m5(String callerId, Integer n, Integer offerA, Integer u, OfferInfo seatInfoA, Integer o){
		logger.trace("execute m5");
		runtime.invokeObj("user", "update", "m5", callerId+"::m5", ++n, seatInfoA, offerA);
		if(o < offerA){
			runtime.invoke("m0", callerId+"::m5", ++n, o);
		}
		else{
			runtime.invoke("m4", callerId+"::m5", ++n, offerA, u);
		}
	}
}