package bftsmart.usecase.max3;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Max3B extends PartitionedObject {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    Integer b = 10;

    public void m3(String callerId, Integer n, Integer a)
    {
        int b = (Integer) runtime.invokeObj("b", "read", "m3", callerId+"::m3", ++n);
        runtime.invoke("m2", callerId+"::m3", ++n, Math.max(a, b));
    }
}
