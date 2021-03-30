package bftsmart.usecase.friendmap;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

public class FriendMapClient extends PartitionedObject implements Client {
    @Override
    public void request(Object... args) {
        friendMap();
    }

    public void friendMap()
    {
//        System.out.println("friendmap request");
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
        runtime.invoke("m1", "friendMap", sequenceNumber++);
    }

    public void ret(String callerId, Integer n, String map)
    {
        String seqNumber = callerId.split("::")[1];
        int id = Integer.valueOf(seqNumber);
        // calculate response time
        runtime.getExecs().put(id, System.currentTimeMillis() - runtime.getExecs().get(id));
        logger.info("response time for call {}: {}", id, runtime.getExecs().get(id));
//        System.out.println(String.format("response time for call %s: %s", id, runtime.getExecs().get(id)));
//        logger.info("return value = {}", x);
//        System.out.println(String.format("return value = %s", map));
        responseReceived++;
        // just for oblivious transfer example since it returns zero after the first call
//        runtime.resetObjectStates();
        objCallLock.lock();
        requestBlock.signalAll();
        objCallLock.unlock();

    }
}
