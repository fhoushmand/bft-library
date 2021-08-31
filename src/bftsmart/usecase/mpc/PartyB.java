package bftsmart.usecase.mpc;

import bftsmart.demo.mpc.MPCClient;
import bftsmart.usecase.PartitionedObject;

public class PartyB extends PartitionedObject {
    public MPCClient  b;

    public void m2(String callerId, Integer n, Integer partialSum)
    {
        System.out.println("execute average m2!");
        logger.trace("execute m2");
        int p2 = (int)runtime.invokeObj("b", "read", "m2", callerId+"::m2", ++n, 2);
        int ap2 = (int)runtime.invokeObj("a", "read", "m2", callerId+"::m2", ++n, 2);
        int cp2 = (int)runtime.invokeObj("c", "read", "m2", callerId+"::m2", ++n, 2);
        int localSum = partialSum + p2 + ap2 + cp2;
        runtime.invoke("m3", callerId+"::m2", ++n, localSum);
    }

}
