package bftsmart.usecase.mpc;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

public class MPCClient extends PartitionedObject implements Client {

    @Override
    public void request(Object... args) {
        average();
    }

    public void average()
    {
        System.out.println("execute average function!");
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
        runtime.invoke("m1", "average", sequenceNumber++);
    }

    public void ret(String callerId, Integer n, Integer x)
    {
        String seqNumber = callerId.split("::")[1];
        int id = Integer.valueOf(seqNumber);
        // calculate response time
        runtime.getExecs().put(id, System.currentTimeMillis() - runtime.getExecs().get(id));
        logger.info("response time for call {}: {}", id, runtime.getExecs().get(id));
//        System.out.println(String.format("response time for call %s: %s", id, runtime.getExecs().get(id)));
//        logger.info("return value = {}", x);
        System.out.println(String.format("return value = %s", x));
        responseReceived++;
        // just for oblivious transfer example since it returns zero after the first call
//        runtime.resetObjectStates();
        objCallLock.lock();
        requestBlock.signalAll();
        objCallLock.unlock();

    }

}
