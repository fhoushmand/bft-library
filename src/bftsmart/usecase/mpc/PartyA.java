package bftsmart.usecase.mpc;

import bftsmart.demo.mpc.MPCClient;
import bftsmart.usecase.PartitionedObject;

public class PartyA extends PartitionedObject {
    public MPCClient  a;

    public void m1(String callerId, Integer n)
    {
        System.out.println("execute average m1!");
        logger.trace("execute m1");
        int p1 = (int)runtime.invokeObj("a", "read", "m1", callerId+"::m1", ++n, 1);
        int bp1 = (int)runtime.invokeObj("b", "read", "m1", callerId+"::m1", ++n, 1);
        int cp1 = (int)runtime.invokeObj("c", "read", "m1", callerId+"::m1", ++n, 1);
        int localSum = p1 + bp1 + cp1;
        System.out.println("calling m2!");
        runtime.invoke("m2", callerId+"::m1", ++n, localSum);
    }

}
