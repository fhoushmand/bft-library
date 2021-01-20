package bftsmart.usecase.obltransfer;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class OblTransferClient extends PartitionedObject implements Client {

    public OblTransferClient(HashMap<Integer, String> hostipMap, String configuration) {
        super(hostipMap, configuration);
    }

    @Override
    public void request(Object... args) {
        transfer((Integer) args[0]);
    }

    public void transfer(Integer x)
    {
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
        logger.info("execute transfer with x={}",x);
        runtime.invoke("m1", "transfer", sequenceNumber++, x); // send m4(x) message to the hosts of m4;
    }

    public void ret(String callerId, Integer n, Integer x)
    {
        String seqNumber = callerId.split("::")[1];
        int id = Integer.valueOf(seqNumber);
        // calculate response time
        runtime.getExecs().put(id, System.currentTimeMillis() - runtime.getExecs().get(id));
        logger.info("response time for call {}: {}", id, runtime.getExecs().get(id));
        System.out.println(String.format("response time for call %s: %s", id, runtime.getExecs().get(id)));
        logger.info("return value = {}", x);
        System.out.println(String.format("return value = %s", x));
        responseReceived++;
        // just for oblivious transfer example since it returns zero after the first call
//        runtime.resetObjectStates();
        objCallLock.lock();
        requestBlock.signalAll();
        objCallLock.unlock();

    }

}
