package bftsmart.usecase;

import bftsmart.demo.register.RegisterClient;
import bftsmart.rmi.RMIRuntime;
import bftsmart.tom.ServiceProxy;

import java.util.HashMap;
import java.util.Scanner;

// 0,1,2,3,4,5,6 -> A servers??
// 7,8,9,10 -> B servers??
public class OTClient extends PartitionedObject {
    RMIRuntime runtime;

    public OTClient(int clientId, int clusterId) throws Exception {
    }

    public static void main(String[] args) throws Exception {
        OTClient otClient = new OTClient(Integer.valueOf(args[0]), 1);
        otClient.runtime = new RMIRuntime(Integer.valueOf(args[0]), otClient);

        Scanner input = new Scanner(System.in);
        while (input.hasNext())
            otClient.transfer(Integer.valueOf(input.nextLine()));
    }

    public void transfer(Integer x)
    {
        runtime.invoke("m4", x, runtime.getMethodsHosts().get("m4")); // send m4(x) message to the hosts of m4;
    }

    public void ret(Integer x)
    {
        System.out.println("returning with value: " + x);
    }


}
