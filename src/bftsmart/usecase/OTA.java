package bftsmart.usecase;

import bftsmart.demo.register.IntegerRegisterServer;
import bftsmart.demo.register.RegisterClient;
import bftsmart.demo.register.BooleanRegisterServer;
import bftsmart.rmi.RMIRuntime;
import com.sun.org.apache.xpath.internal.operations.Bool;


public class OTA extends PartitionedObject {
    public Integer i1 = 10;
    public Boolean a = false;


    public void m4(Integer x)
    {
        System.out.println("calling m4 locally with x="+x);
        System.out.println("isAccessed value: " + (Boolean)runtime.invokeObj("a", "read"));
        if(!(Boolean)runtime.invokeObj("a", "read", 0)) {
            System.out.println("if branch, calling m3...");
            runtime.invoke("m3", x);
        }
        else {
            System.out.println("else branch, calling ret...");
            runtime.invoke("ret", 0); //send ret(0) message to the client;
        }
    }

    public void m3(Integer x)
    {
        System.out.println("calling m3 locally with x="+x);
        //the return value of this call should be recorded in order
        //to avoid multiple execution of object method calls
        //must translate to invocation from runtime
//        a.write(true);
        runtime.invokeObj("a", "write", true);

        System.out.println("isAccessed value: " + runtime.invokeObj("a", "read", null));
        if(x != 0) {
            System.out.println("if branch, calling m2...");
            runtime.invoke("m2", 0); // send m2() message to the hosts of m2;
        }
        else {
            System.out.println("else branch, calling m1...");
            runtime.invoke("m1", 0);
        }
    }

    public void m1()
    {
        System.out.println("calling m1 locally");
        System.out.println("calling ret...");
        runtime.invoke("ret", (Integer) runtime.invokeObj("i1", "read", 0)); //send ret(i1.read()) message to the client;
    }
}
