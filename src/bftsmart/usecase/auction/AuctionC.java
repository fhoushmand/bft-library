package bftsmart.usecase.auction;

import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class AuctionC extends PartitionedObject {

    public UserAgentClient userAgent;

    public AuctionC(HashMap<Integer, String> hostipMap, String configuration) {
        super(hostipMap, configuration);
    }

    public void m1(String callerId, Integer n, Integer offer)
    {
        int user = (Integer) runtime.invokeObj("userAgent", "read", "m1", callerId+"::m1",  ++n);
        runtime.invoke("m2", callerId+"::m1", ++n, user, offer);
    }

    public void m3(String callerId, Integer n, Integer user, Integer offer, OfferInfo offerA)
    {
        runtime.invokeObj("userAgent", "updateOffer", "m3", callerId+"::m3",  ++n, offerA);
        if(offer < offerA.offer)
            runtime.invoke("m4", callerId+"::m3", ++n, offer);
        else
            runtime.invoke("m5", callerId+"::m3", ++n, user, offerA.offer);
    }

    public void m4(String callerId, Integer n, Integer offer)
    {
        OfferInfo winner = (OfferInfo) runtime.invokeObj("userAgent", "declareWinner", "m3", callerId+"::m3",  ++n, offer);
        runtime.invoke("ret", callerId+"::m4", ++n, winner);
    }

    public void m6(String callerId, Integer n, OfferInfo offerB, Integer offerA)
    {
        runtime.invokeObj("userAgent", "updateOffer", "m6", callerId+"::m6",  ++n, offerB);
        if(offerB.offer < offerA)
            runtime.invoke("m1", callerId+"::m6", ++n, offerB.offer);
        else
            runtime.invoke("m4", callerId+"::m6", ++n, offerA);
    }


}