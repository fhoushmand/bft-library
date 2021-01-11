package bftsmart.usecase.max3;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Max3Client extends PartitionedObject {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void max()
    {
        objCallLock.lock();
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
        logger.info("execute max()");
        runtime.invoke("m4", "max", sequenceNumber); // send m4(x) message to the hosts of m4;
    }

    public void ret(Integer max)
    {
        // calculate response time
        runtime.getExecs().put(sequenceNumber, System.currentTimeMillis() - runtime.getExecs().get(sequenceNumber));
        logger.info("response time for call {}: {}", sequenceNumber, runtime.getExecs().get(sequenceNumber));
        System.out.println(String.format("response time for call %s: %s", sequenceNumber, runtime.getExecs().get(sequenceNumber)));
        logger.info("return value = {}", max);
        System.out.println(String.format("return value = %s", max));
        sequenceNumber++;
        // just for oblivious transfer example since it returns zero after the first call
        runtime.resetObjectStates();
        objCallLock.unlock();
    }
}
