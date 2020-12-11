package bftsmart.usecase;

import bftsmart.demo.register.IntegerRegisterServer;
import bftsmart.demo.register.RegisterClient;
import bftsmart.demo.register.BooleanRegisterServer;
import bftsmart.rmi.RMIRuntime;

// 0,1,2,3,4,5,6 -> A servers??
// 7,8,9,10 -> B servers??
public class OTB extends PartitionedObject{
    RegisterClient<Integer> i2;
    RegisterClient<Boolean> a;

    RMIRuntime runtime;


    public OTB(int p){

        // initialize the bft library to replicate object fields
        IntegerRegisterServer.main(new String[]{"Integer", String.valueOf(p), "2"}); //replication of i2
        BooleanRegisterServer.main(new String[]{"Boolean", String.valueOf(p), "3"}); //replication of a

        i2 = new RegisterClient<>(p, 2);
        a = new RegisterClient<>(p, 3);
    }


    public static void main(String[] args) throws Exception {
        OTB otb = new OTB(Integer.valueOf(args[0]));
        otb.runtime = new RMIRuntime(Integer.valueOf(args[0]), otb);
    }

    public void m2()
    {
        runtime.invoke("ret", i2.read(), runtime.getMethodsHosts().get("ret")); //send ret(i2.read()) message to client;
    }
}
