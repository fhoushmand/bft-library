package bftsmart.usecase.auction;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class AuctionB extends PartitionedObject {
    public AirlineAgentClient B;

    public void m4(String callerId, Integer n, Integer user, Integer offerA)
    {
        //System.out.println("execute m4!");
        OfferInfo offerB = (OfferInfo) runtime.invokeObj("B", "makeOfferB1", "m4", callerId+"::m4", ++n, user, offerA);
//        System.out.println(offerB.toString() + ":" + offerB.seatInfo + "," + offerB.offer);
        runtime.invoke("m3", callerId+"::m4", ++n, offerB, offerA, user);
    }

    public void m3(String callerId, Integer n, OfferInfo offerBI, Integer offerA, Integer user)
    {
        //System.out.println("execute m4!");
        Integer offerB = (Integer) runtime.invokeObj("B", "makeOfferB2", "m3", callerId+"::m3", ++n, user, offerA);
//        System.out.println(offerB.toString() + ":" + offerB.seatInfo + "," + offerB.offer);
        runtime.invoke("m2", callerId+"::m4", ++n, offerB, offerA, offerBI);
    }
}