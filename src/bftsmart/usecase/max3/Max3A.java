package bftsmart.usecase.max3;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Max3A extends PartitionedObject {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    Integer a = 5;

    public void m4(String callerId, Integer n)
    {
        runtime.invoke("m3", callerId+"::m4", ++n, runtime.invokeObj("a", "read", "m4", callerId+"::m4", ++n));
    }
}
