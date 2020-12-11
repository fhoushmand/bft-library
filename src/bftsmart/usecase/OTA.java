package bftsmart.usecase;

import bftsmart.demo.register.IntegerRegisterServer;
import bftsmart.demo.register.RegisterClient;
import bftsmart.demo.register.BooleanRegisterServer;
import bftsmart.rmi.RMIRuntime;


public class OTA extends PartitionedObject {
    RegisterClient<Integer> i1;
    RegisterClient<Boolean> a;

    RMIRuntime runtime;


    public OTA(int p){

        // initialize the bft library to replicate object fields
        IntegerRegisterServer.main(new String[]{"Integer", String.valueOf(p), "1"}); //replication of i1
        BooleanRegisterServer.main(new String[]{"Boolean", String.valueOf(p), "3"}); //replication of a

        i1 = new RegisterClient<>(p, 1);
        a = new RegisterClient<>(p, 3);
    }


    public static void main(String[] args) throws Exception {
        OTA ota = new OTA(Integer.valueOf(args[0]));
        ota.runtime = new RMIRuntime(Integer.valueOf(args[0]), ota);
    }


    public void m4(Integer x)
    {
        if(a.read())
            m3(x);
        else
            runtime.invoke("ret", 0, runtime.getMethodsHosts().get("ret")); //send ret(0) message to the client;
    }

    public void m3(Integer x)
    {
        a.write(true);
        if(x != 0)
            runtime.invoke("m2", 0, runtime.getMethodsHosts().get("m2")); // send m2() message to the hosts of m2;
        else
            m1();
    }

    public void m1()
    {
        runtime.invoke("ret", i1.read(), runtime.getMethodsHosts().get("ret")); //send ret(i1.read()) message to the client;
    }
}
