package bftsmart.usecase.oblivioustransfer;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11
public class OTB extends PartitionedObject {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Integer i2 = 5;
    public Boolean a = false;

    public OTB(HashMap<Integer, String> hostipMap) {
        super(hostipMap);
    }

    public void m4(String callerId, Integer n, Integer x)
    {
        logger.trace("execute m4 with x={}",x);
        if(!(Boolean)runtime.invokeObj("a", "read", "m4", callerId+"::m4", ++n))
            runtime.invoke("m3", callerId+"::m4", ++n, x);
        else
            runtime.invoke("ret", callerId+"::m4", ++n, 0); //send ret(0) message to the client;
    }

    public void m3(String callerId, Integer n, Integer x)
    {
        logger.trace("execute m3 with x={}",x);
        runtime.invokeObj("a", "write", "m3", callerId+"::m3", ++n, true);
//        logger.trace("a={}",runtime.invokeObj("a", "write", "m3", callerId+"::m3", ++n, true));
        if(x != 0)
            runtime.invoke("m2", callerId+"::m3", ++n); // send m2() message to the hosts of m2;
        else
            runtime.invoke("m1", callerId+"::m3", ++n);
    }

    public void m2(String callerId, Integer n)
    {
        logger.trace("execute m2");
        int o = (Integer) runtime.invokeObj("i2", "read", "m2", callerId+"::m2", ++n);
        runtime.invoke("ret", callerId+"::m2", ++n, o); //send ret(i2.read()) message to client;
    }
}
