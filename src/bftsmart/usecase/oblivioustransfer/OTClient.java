package bftsmart.usecase.oblivioustransfer;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11
public class OTClient extends PartitionedObject implements Client {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    public OTClient(HashMap<Integer, String> hostipMap, String configuration) {
        super(hostipMap, configuration);
    }

    @Override
    public void request(Object... args) {
        transfer((Integer) args[0]);
    }

    public void transfer(Integer x)
    {
        objCallLock.lock();
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
        logger.info("execute transfer with x={}",x);
        runtime.invoke("m4", "transfer", sequenceNumber, x); // send m4(x) message to the hosts of m4;

    }

    public void ret(Integer x)
    {
        // calculate response time
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis() - runtime.getExecs().get(sequenceNumber));
        logger.info("response time for call {}: {}", sequenceNumber, runtime.getExecs().get(sequenceNumber));
        System.out.println(String.format("response time for call %s: %s", sequenceNumber, runtime.getExecs().get(sequenceNumber)));
        logger.info("return value = {}", x);
        System.out.println(String.format("return value = %s", x));
        sequenceNumber++;
        // just for oblivious transfer example since it returns zero after the first call
        runtime.resetObjectStates();
        objCallLock.unlock();
    }


}
