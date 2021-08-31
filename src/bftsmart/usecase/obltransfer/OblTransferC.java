package bftsmart.usecase.obltransfer;

import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class OblTransferC extends PartitionedObject {
    public void m3(String callerId, Integer n, Integer x)
    {
        Boolean x1 = (Boolean) runtime.invokeObj("a", "read", "m3", callerId+"::m3", ++n);
        if(!x1)
        {
            runtime.invoke("m2", callerId+"::m3", ++n, x);
        }
        else
            runtime.invoke("ret", callerId+"::m3", ++n, 0);
    }

    public void m2(String callerId, Integer n, Integer x)
    {
        // just to mimick the resetting of the object state in order not to return 0 for subseq calls
        runtime.invokeObj("a", "write", "m2", callerId+"::m2", ++n, Boolean.FALSE);
        runtime.invoke("m1", callerId+"::m2", ++n, x);

    }

    public void m1(String callerId, Integer n, Integer x)
    {
        Integer temp1 = (Integer) runtime.invokeObj("i1", "read", "m1", callerId+"::m1", ++n);
        runtime.invoke("m0", callerId+"::m1", ++n, x ,temp1);
    }

    public void m0(String callerId, Integer n, Integer x, Integer temp1)
    {
        Integer temp2 = (Integer) runtime.invokeObj("i2", "read", "m0", callerId+"::m0", ++n);
        if(x == 1)
            runtime.invoke("ret", callerId+"::m0", ++n, temp1);
        else
            runtime.invoke("ret", callerId+"::m0", ++n, temp2);
    }
}
