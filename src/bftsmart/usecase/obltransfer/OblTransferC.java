package bftsmart.usecase.obltransfer;

import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class OblTransferC extends PartitionedObject {
    public void m1(String callerId, Integer n, Integer x)
    {
        Boolean x1 = (Boolean) runtime.invokeObj("r", "read", "m1", callerId+"::m1", ++n);
        if(!x1)
        {
            // just to mimick the resetting of the object state in order not to return 0 for subseq calls
            runtime.invokeObj("r", "write", "m1", callerId+"::m1", ++n, Boolean.FALSE);

            Integer temp1 = (Integer) runtime.invokeObj("r1", "read", "m1", callerId+"::m1", ++n);
            Integer temp2 = (Integer) runtime.invokeObj("r2", "read", "m1", callerId+"::m1", ++n);

            if(x == 1)
                runtime.invoke("ret", callerId+"::m1", ++n, temp1);
            else
                runtime.invoke("ret", callerId+"::m1", ++n, temp2);
        }
        else
            runtime.invoke("ret", callerId+"::m1", ++n, 0);
    }
}
