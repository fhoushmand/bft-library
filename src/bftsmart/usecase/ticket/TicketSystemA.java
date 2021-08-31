package bftsmart.usecase.ticket;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class TicketSystemA extends PartitionedObject {
    public AirlineAgentClient airline;

    public void m9(String callerId, Integer n, Integer ticketNum)
    {
        //System.out.println("start executing m9");
        Integer tNum = (Integer) runtime.invokeObj("customer", "ticketNum", "m9", callerId+"::m9", ++n);
        //System.out.println("execute m9 with ticket num: " + tNum);
        runtime.invoke("m8", callerId+"::m9", ++n, ticketNum);
    }

    public void m8(String callerId, Integer n, Integer ticketNum)
    {
        TicketInfo ticketInfo = (TicketInfo) runtime.invokeObj("airline", "getPrice", "m8", callerId+"::m8", ++n, ticketNum);
        runtime.invoke("m6", callerId+"::m8", ++n, ticketInfo, ticketNum);
    }

    public void m6(String callerId, Integer n, TicketInfo info, Integer ticketNum)
    {
        runtime.invokeObj("customer", "updateInfo", "m6", callerId+"::m6", ++n, info);
        runtime.invoke("m5", callerId+"::m6", ++n, info.price, ticketNum);

    }

    public void m5(String callerId, Integer n, Integer price, Integer ticketNum)
    {
        Integer ID = (Integer) runtime.invokeObj("customer", "getID", "m5", callerId+"::m5", ++n);
        runtime.invoke("m4", callerId+"::m4", ++n, price, ticketNum, ID);
    }

    public void m1(String callerId, Integer n, Integer price, Integer ticketNum)
    {
        runtime.invokeObj("airline", "decSeat", "m1", callerId+"::m1", ++n, ticketNum);
        runtime.invoke("m0", callerId+"::m1", ++n, price);
    }
}
