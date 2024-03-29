package bftsmart.usecase.onetimetransfer_optimized;

import bftsmart.demo.register.BooleanRegisterClient;
import bftsmart.demo.register.IntegerRegisterClient;
import bftsmart.usecase.PartitionedObject;

// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11
public class OTTB extends PartitionedObject {
    public IntegerRegisterClient i2;
    public BooleanRegisterClient a;


    public void m3(String callerId, Integer n, Integer x)
    {
        logger.trace("execute m3 with x={}",x);
        if(!(Boolean)runtime.invokeObj("a", "read", "m3", callerId+"::m3", ++n))
            runtime.invoke("m2", callerId+"::m3", ++n, x);
        else
            runtime.invoke("ret", callerId+"::m3", ++n, 0); //send ret(0) message to the client;
    }

    public void m2(String callerId, Integer n, Integer x)
    {
        logger.trace("execute m2 with x={}",x);
        runtime.invokeObj("a", "write", "m2", callerId+"::m2", ++n, false);
        if(x != 0)
            runtime.invoke("m0", callerId+"::m2", ++n); // send m2() message to the hosts of m2;
        else
            runtime.invoke("m1", callerId+"::m2", ++n);
    }

    public void m1(String callerId, Integer n)
    {
        logger.trace("execute m1");
        int o = (Integer) runtime.invokeObj("i2", "read", "m1", callerId+"::m1", ++n);
        runtime.invoke("ret", callerId+"::m1", ++n, o); //send ret(i2.read()) message to client;
    }
}
