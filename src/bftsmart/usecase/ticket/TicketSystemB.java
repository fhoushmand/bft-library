package bftsmart.usecase.ticket;

import bftsmart.demo.bankagent.BankAgentClient;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.usecase.PartitionedObject;
import com.yahoo.ycsb.generator.IntegerGenerator;

import java.util.HashMap;

public class TicketSystemB extends PartitionedObject {
    public BankAgentClient bank;

    public void m4(String callerId, Integer n, Integer price, Integer ticketNum, Integer ID)
    {
        IntIntPair cashbackBalance = (IntIntPair) runtime.invokeObj("bank", "getBalance", "m4", callerId+"::m4", ++n, ID);
        runtime.invoke("m2", callerId+"::m4", ++n, cashbackBalance, price, ticketNum);
    }

    public void m2(String callerId, Integer n, IntIntPair cashbackBalance, Integer price, Integer ticketNum)
    {
        runtime.invokeObj("customer", "updatePayment", "m2", callerId+"::m2", ++n, cashbackBalance);
        if(price <= cashbackBalance.getSecond())
            runtime.invoke("m1", callerId+"::m2", ++n, price, ticketNum);
        else
            runtime.invoke("ret", callerId+"::m2", ++n, Boolean.FALSE);
    }

    public void m0(String callerId, Integer n, Integer price)
    {
        runtime.invokeObj("bank", "decBalance", "m0", callerId+"::m0", ++n, price);
        runtime.invoke("ret", callerId+"::m0", ++n, Boolean.TRUE);
    }
}
