package bftsmart.usecase;

import bftsmart.rmi.RMIRuntime;

public class PartitionedObject {
    RMIRuntime runtime;


    public PartitionedObject() {
    }

    public void setRuntime(RMIRuntime runtime) {
        this.runtime = runtime;
    }
}
