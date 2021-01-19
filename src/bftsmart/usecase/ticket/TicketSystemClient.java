package bftsmart.usecase.ticket;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;
import bftsmart.usecase.auction.OfferInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class TicketSystemClient extends PartitionedObject implements Client {

    public TicketSystemClient(HashMap<Integer, String> hostipMap, String configuration) {
        super(hostipMap, configuration);
    }

    public TreeMap<Integer,ArrayList<Long>> requestresponseTimes = new TreeMap<>();


    @Override
    public void request(Object... args) {
        buyTicket((Integer)args[0] );
    }

    public void buyTicket(Integer ticket)
    {
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
        logger.info("{}: execute buyTicket {}", sequenceNumber, ticket);
        runtime.invoke("m2", "buyTicket", sequenceNumber++, ticket);
    }

    public void ret(String callerId, Integer n, Boolean bought)
    {
        String seqNumber = callerId.split("::")[1];
        int id = Integer.valueOf(seqNumber);
        // calculate response time
        long resTime = System.currentTimeMillis() - runtime.getExecs().get(id);
        runtime.getExecs().put(id, resTime);
        logger.info("response time for call {}: {}", id, runtime.getExecs().get(id));
//        System.out.println(String.format("response time for call %s: %s", id, runtime.getExecs().get(id)));
        logger.info("return value = {}", bought);
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