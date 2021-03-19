package bftsmart.usecase.auction;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

public class AuctionClient extends PartitionedObject implements Client {
//    public TreeSet<Long> responseTimes = new TreeSet<>();
//    TreeMap<Integer,Integer> requests = new TreeMap<>();
//    public TreeMap<Integer,ArrayList<Long>> requestresponseTimes = new TreeMap<>();

    @Override
    public void request(Object... args) {
        auction((Integer) args[0]);
    }

    public void auction(Integer offer)
    {
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
//        requests.put(sequenceNumber, offer);
        logger.info("execute auction with x={}",offer);
        runtime.invoke("m1", "auction", sequenceNumber++, offer);
    }

    public void ret(String callerId, Integer n, OfferInfo offer)
    {
        String seqNumber = callerId.split("::")[1];
        int id = Integer.valueOf(seqNumber);
        // calculate response time
        long resTime = System.currentTimeMillis() - runtime.getExecs().get(id);
        runtime.getExecs().put(id, resTime);
//        responseTimes.add(resTime);
//        ArrayList<Long> resTimes = requestresponseTimes.getOrDefault(requests.get(id), new ArrayList<>());
//        resTimes.add(resTime);
//        requestresponseTimes.put(requests.get(id), resTimes);
        logger.info("response time for call {}: {}", id, runtime.getExecs().get(id));
//        System.out.println(String.format("response time for call %s: %s", id, runtime.getExecs().get(id)));
        logger.info("return value = {}", offer.getAsString());
//        System.out.println(String.format("return value = %s", offer.getAsString()));
        responseReceived++;
        // just for oblivious transfer example since it returns zero after the first call
//        runtime.resetObjectStates();
        logger.info("-------------");
        objCallLock.lock();
        requestBlock.signalAll();
        objCallLock.unlock();
    }
}