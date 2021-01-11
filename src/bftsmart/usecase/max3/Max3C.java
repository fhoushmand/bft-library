package bftsmart.usecase.max3;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Max3C extends PartitionedObject {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    Integer c = 15;

    public void m2(String callerId, Integer n, Integer max)
    {
        int c = (Integer) runtime.invokeObj("c", "read", "m2", callerId+"::m2", ++n);
        runtime.invoke("ret", callerId+"::m2", ++n, Math.max(max, c));
    }
}
