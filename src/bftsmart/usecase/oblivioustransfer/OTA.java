package bftsmart.usecase.oblivioustransfer;

import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11
public class OTA extends PartitionedObject {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public Integer i1 = 10;
    public Boolean a = false;


    public void m4(Integer x)
    {
        logger.trace("execute m4 with x={}",x);
        if(!(Boolean)runtime.invokeObj("a", "read")) {
            runtime.invoke("m3", x);
        }
        else {
            runtime.invoke("ret", 0); //send ret(0) message to the client;
        }
    }

    public void m3(Integer x)
    {
        logger.trace("execute m3 with x={}",x);
        //the return value of this call should be recorded in order
        //to avoid multiple execution of object method calls
        //must translate to invocation from runtime
        runtime.invokeObj("a", "write", true);

        logger.trace("a={}",runtime.invokeObj("a", "read"));
        if(x != 0)
            runtime.invoke("m2", 0); // send m2() message to the hosts of m2;
        else
            runtime.invoke("m1", 0);
    }

    public void m1()
    {
        logger.trace("execute m1");
        runtime.invoke("ret", (Integer) runtime.invokeObj("i1", "read")); //send ret(i1.read()) message to the client;
    }
}
