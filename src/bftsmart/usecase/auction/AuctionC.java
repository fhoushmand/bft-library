package bftsmart.usecase.auction;

import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class AuctionC extends PartitionedObject {
    public UserAgentClient user;

    public void m5(String callerId, Integer n, Integer user, Integer offer, OfferInfo offerA)
    {
        //System.out.println("execute m5!");
        runtime.invokeObj("user", "updateOffer", "m5", callerId+"::m5",  ++n, offerA);
        if(offer < offerA.offer)
            runtime.invoke("m0", callerId+"::m5", ++n, offer);
        else
            runtime.invoke("m4", callerId+"::m5", ++n, user, offerA.offer);
    }

    public void m0(String callerId, Integer n, Integer offer)
    {
        //System.out.println("execute m0!");
        OfferInfo winner = (OfferInfo) runtime.invokeObj("user", "declareWinner", "m0", callerId+"::m0",  ++n, offer);
        runtime.invoke("ret", callerId+"::m0", ++n, winner);
    }

    public void m2(String callerId, Integer n, OfferInfo offerB, Integer offerA)
    {
        //System.out.println("execute m2!");
        runtime.invokeObj("user", "updateOffer", "m2", callerId+"::m2",  ++n, offerB);
        if(offerB.offer < offerA){
            //System.out.println("gonna execute m8!");
            runtime.invoke("m8", callerId+"::m2", ++n, offerB.offer);
        }
        else {
            //System.out.println("gonna execute m0!");
            runtime.invoke("m0", callerId+"::m2", ++n, offerA);
        }
    }
}