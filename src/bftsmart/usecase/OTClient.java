package bftsmart.usecase;

import bftsmart.demo.register.RegisterClient;
import bftsmart.rmi.RMIRuntime;
import bftsmart.tom.ServiceProxy;

import java.util.HashMap;
import java.util.Scanner;

// 0,1,2,3,4,5,6 -> A servers??
// 7,8,9,10 -> B servers??
public class OTClient extends PartitionedObject {


    public void transfer(Integer x)
    {
        runtime.invoke("m4", x); // send m4(x) message to the hosts of m4;
    }

    public void ret(Integer x)
    {
        System.out.println("returning with value: " + x);
    }


}
