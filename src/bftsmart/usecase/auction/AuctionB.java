package bftsmart.usecase.auction;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class AuctionB extends PartitionedObject {

    public AirlineAgentClient agentB;

//    public AuctionB(HashMap<Integer, String> hostipMap, String configuration) {
//        super(hostipMap, configuration);
//    }

    public void m5(String callerId, Integer n, Integer user, Integer offerA)
    {
        OfferInfo offerB = (OfferInfo) runtime.invokeObj("agentB", "makeOfferB", "m5", callerId+"::m5", ++n, user, offerA);
//        System.out.println(offerB.toString() + ":" + offerB.seatInfo + "," + offerB.offer);
        runtime.invoke("m6", callerId+"::m5", ++n, offerB, offerA);
    }
}