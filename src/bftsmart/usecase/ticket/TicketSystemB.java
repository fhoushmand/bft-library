package bftsmart.usecase.ticket;

import bftsmart.demo.bankagent.BankAgentClient;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class TicketSystemB extends PartitionedObject {

    public BankAgentClient bankAgent;

//    public TicketSystemB(HashMap<Integer, String> hostipMap, String configuration) {
//        super(hostipMap, configuration);
//    }

    public void m5(String callerId, Integer n, Integer price)
    {
        IntIntPair cashbackBalance = (IntIntPair) runtime.invokeObj("bankAgent", "getBalance", "m5", callerId+"::m5", ++n);
        runtime.invoke("m6", callerId+"::m5", ++n, cashbackBalance, price);
    }

    public void m8(String callerId, Integer n, Integer price)
    {
        runtime.invokeObj("bankAgent", "decBalance", "m8", callerId+"::m8", ++n, price);
        runtime.invoke("ret", callerId+"::m8", ++n, Boolean.TRUE);
    }
}
