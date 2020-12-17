package bftsmart.usecase.oblivioustransfer;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11
public class OTClient extends PartitionedObject {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void transfer(Integer x)
    {
        logger.trace("execute transfer with x={}",x);
        runtime.invoke("m4", x); // send m4(x) message to the hosts of m4;
    }

    public void ret(Integer x)
    {
        System.out.println("value = " + x);
    }


}
