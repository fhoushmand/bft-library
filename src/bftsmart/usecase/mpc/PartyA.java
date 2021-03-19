package bftsmart.usecase.mpc;

import bftsmart.demo.register.MPCClient;
import bftsmart.usecase.PartitionedObject;

public class PartyA extends PartitionedObject {
    public MPCClient i1;

    public void m1(String callerId, Integer n)
    {
        logger.trace("execute m1");

    }

}
