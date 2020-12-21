package bftsmart.usecase.oblivioustransfer;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11
public class OTB extends PartitionedObject {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Integer i2 = 5;
    public Boolean a = false;

    public void m4(Integer x, String callerId, Integer n)
    {
//        logger.trace("execute m4 with x={}",x);
        if(!(Boolean)runtime.invokeObj("a", "read", callerId+"::m4", ++n))
            runtime.invoke("m3", x, callerId+"::m4", ++n);
        else
            runtime.invoke("ret", 0, callerId+"::m4", ++n); //send ret(0) message to the client;
    }

    public void m3(Integer x, String callerId, Integer n)
    {
//        logger.trace("execute m3 with x={}",x);
        //the return value of this call should be recorded in order
        //to avoid multiple execution of object method calls
        //must translate to invocation from runtime
        runtime.invokeObj("a", "write", true, callerId+"::m3", ++n);

//        logger.trace("a={}",runtime.invokeObj("a", "read", callerId));
        if(x != 0)
            runtime.invoke("m2", callerId+"::m3", ++n); // send m2() message to the hosts of m2;
        else
            runtime.invoke("m1", callerId+"::m3", ++n);
    }

    public void m2(String callerId, Integer n)
    {
//        logger.trace("execute m2");
        int o = (Integer) runtime.invokeObj("i2", "read", callerId+"::m2", ++n);
        runtime.invoke("ret", o, callerId+"::m2", ++n); //send ret(i2.read()) message to client;
    }

}
