package bftsmart.usecase.auction;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class AuctionA extends PartitionedObject {
    public AirlineAgentClient agentA;

    public void m2(String callerId, Integer n, Integer user, Integer offer)
    {
        OfferInfo offerA = (OfferInfo) runtime.invokeObj("agentA", "makeOfferA", "m2", callerId+"::m2", ++n, user, offer);
//        System.out.println(offerA.toString() + ":" + offerA.seatInfo + "," + offerA.offer);
        runtime.invoke("m3", callerId+"::m2", ++n, user, offer, offerA);
    }
}