package bftsmart.usecase.auction;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class AuctionA extends PartitionedObject {
    public AirlineAgentClient A;

    public void m8(String callerId, Integer n, Integer offer)
    {
        //System.out.println("execute m8!");
        int user = (Integer) runtime.invokeObj("user", "read", "m8", callerId+"::m8",  ++n);
        runtime.invoke("m7", callerId+"::m8", ++n, user, offer);
    }

    public void m7(String callerId, Integer n, Integer user, Integer offer)
    {
        //System.out.println("execute m7!");
        OfferInfo offerA = (OfferInfo) runtime.invokeObj("A", "makeOfferA1", "m7", callerId+"::m7", ++n, user, offer);
//        System.out.println(offerA.toString() + ":" + offerA.seatInfo + "," + offerA.offer);
        runtime.invoke("m6", callerId+"::m7", ++n, user, offer, offerA);
    }

    public void m6(String callerId, Integer n, Integer user, Integer offer, OfferInfo offerIA)
    {
        //System.out.println("execute m7!");
        Integer offerA = (Integer) runtime.invokeObj("A", "makeOfferA2", "m6", callerId+"::m6", ++n, user, offer);
//        System.out.println(offerA.toString() + ":" + offerA.seatInfo + "," + offerA.offer);
        runtime.invoke("m5", callerId+"::m6", ++n, user, offer, offerIA, offerA);
    }
}