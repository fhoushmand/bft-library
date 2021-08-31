package bftsmart.usecase.mpc;

import bftsmart.demo.mpc.MPCClient;
import bftsmart.usecase.PartitionedObject;

public class PartyC extends PartitionedObject {
    public MPCClient  c;

    public void m3(String callerId, Integer n, Integer partialSum)
    {
        System.out.println("execute average m3!");
        logger.trace("execute m3");
        int p3 = (int)runtime.invokeObj("b", "read", "m3", callerId+"::m3", ++n, 3);
        int ap3 = (int)runtime.invokeObj("a", "read", "m3", callerId+"::m3", ++n, 3);
        int bp3 = (int)runtime.invokeObj("c", "read", "m3", callerId+"::m3", ++n, 3);
        int localSum = partialSum + p3 + ap3 + bp3;
        runtime.invoke("ret", callerId+"::m3", ++n, localSum/3);
    }
}
