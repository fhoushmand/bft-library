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
        //System.out.println("start executing m4");
        //IntIntPair cashbackBalance = (IntIntPair) runtime.invokeObj("bank", "getBalance", "m4", callerId+"::m4", ++n, ID);
        Integer cashback = (Integer) runtime.invokeObj("bank", "getBalance1", "m4", callerId+"::m4", ++n, ID);
        runtime.invoke("m3", callerId+"::m4", ++n, cashback, price, ticketNum, ID);
    }

    public void m3(String callerId, Integer n, Integer cashabck, Integer price, Integer ticketNum, Integer ID)
    {
        //System.out.println("start executing m3");
        //IntIntPair cashbackBalance = (IntIntPair) runtime.invokeObj("bank", "getBalance", "m4", callerId+"::m4", ++n, ID);
        Integer balance = (Integer) runtime.invokeObj("bank", "getBalance2", "m3", callerId+"::m3", ++n, ID);
        runtime.invoke("m2", callerId+"::m3", ++n, cashabck, balance, price, ticketNum);
    }

    public void m2(String callerId, Integer n, Integer c, Integer b, Integer price, Integer ticketNum)
    {
        //System.out.println("start executing m2");
        runtime.invokeObj("customer", "updatePayment", "m2", callerId+"::m2", ++n, c, b);
        if(price <= b)
            runtime.invoke("m1", callerId+"::m2", ++n, price, ticketNum);
        else
            runtime.invoke("ret", callerId+"::m2", ++n, Boolean.FALSE);
    }

    public void m0(String callerId, Integer n, Integer price)
    {
        //System.out.println("start executing m0");
        runtime.invokeObj("bank", "decBalance", "m0", callerId+"::m0", ++n, price);
        runtime.invoke("ret", callerId+"::m0", ++n, Boolean.TRUE);
    }
}
