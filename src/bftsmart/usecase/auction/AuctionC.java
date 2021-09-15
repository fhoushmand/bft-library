package bftsmart.usecase.auction;

import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class AuctionC extends PartitionedObject {
    public UserAgentClient user;

    public void m5(String callerId, Integer n, Integer user, Integer offer, OfferInfo offerIA, Integer offerA)
    {
        //System.out.println("execute m5!");
        runtime.invokeObj("user", "update", "m5", callerId+"::m5",  ++n, offerIA, offerA);
        if(offer < offerA)
            runtime.invoke("m0", callerId+"::m5", ++n, offer);
        else
            runtime.invoke("m4", callerId+"::m5", ++n, user, offerA);
    }

    public void m0(String callerId, Integer n, Integer offer)
    {
        //System.out.println("execute m0!");
        OfferInfo winner = (OfferInfo) runtime.invokeObj("user", "declareWinner", "m0", callerId+"::m0",  ++n, offer);
        runtime.invoke("ret", callerId+"::m0", ++n, winner);
    }

    public void m2(String callerId, Integer n, Integer offerB, Integer offerA, OfferInfo offerBI)
    {
        //System.out.println("execute m2!");
        runtime.invokeObj("user", "update", "m2", callerId+"::m2",  ++n, offerBI, offerB);
        if(offerB < offerA){
            //System.out.println("gonna execute m8!");
            runtime.invoke("m8", callerId+"::m2", ++n, offerB);
        }
        else {
            //System.out.println("gonna execute m0!");
            runtime.invoke("m0", callerId+"::m2", ++n, offerA);
        }
    }
}