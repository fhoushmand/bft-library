package bftsmart.usecase.oblivioustransfer;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11


/**
 * if x == 0 for the first time -> return value = i1 (10)
 * if x != 0 for the first time -> return value = i2 (5)
 * for the later invocations -> return value = 0
 */
//TODO should be able to move the arguments of the partitioned methods to the runtime -- (how?)
public class OTA extends PartitionedObject {
    public Integer i1 = 10;
    public Boolean a = false;

//    public OTA(HashMap<Integer, String> hostipMap, String configuration) {
//        super(hostipMap, configuration);
//    }

    public void m4(String callerId, Integer n, Integer x)
    {
        logger.trace("execute m4 with x={}",x);
        if(!(Boolean)runtime.invokeObj("a", "read", "m4", callerId+"::m4", ++n)) {
            runtime.invoke("m3", callerId+"::m4", ++n, x);
        }
        else {
            runtime.invoke("ret", callerId+"::m4", ++n, 0); //send ret(0) message to the client;
        }
    }

    public void m3(String callerId, Integer n, Integer x)
    {
        logger.trace("execute m3 with x={}",x);
        //TODO changed to false to mimick the reset functionality
        runtime.invokeObj("a", "write", "m3", callerId+"::m3", ++n, false);
        if(x != 0)
            runtime.invoke("m2", callerId+"::m3", ++n); // send m2() message to the hosts of m2;
        else
            runtime.invoke("m1", callerId+"::m3", ++n);
    }

    public void m1(String callerId, Integer n)
    {
        logger.trace("execute m1");
        int o = (Integer) runtime.invokeObj("i1", "read", "m1",callerId+"::m1", ++n);
        runtime.invoke("ret", callerId+"::m1", ++n, o); //send ret(i1.read()) message to the client;
    }
}
