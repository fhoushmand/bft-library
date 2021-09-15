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
        //System.out.println("start executing m8");
        String ticketSchedule = (String) runtime.invokeObj("airline", "getPrice1", "m8", callerId+"::m8", ++n, ticketNum);
        runtime.invoke("m7", callerId+"::m8", ++n, ticketSchedule, ticketNum);
    }

    public void m7(String callerId, Integer n, String schedule, Integer ticketNum)
    {
        //System.out.println("start executing m7");
        Integer ticketPrice = (Integer) runtime.invokeObj("airline", "getPrice2", "m7", callerId+"::m7", ++n, ticketNum);
        runtime.invoke("m6", callerId+"::m7", ++n, schedule, ticketPrice, ticketNum);
    }

    public void m6(String callerId, Integer n, String schedule, Integer price, Integer ticketNum)
    {
        //System.out.println("start executing m6");
        runtime.invokeObj("customer", "updateInfo", "m6", callerId+"::m6", ++n, schedule, price);
        runtime.invoke("m5", callerId+"::m6", ++n, price, ticketNum);

    }

    public void m5(String callerId, Integer n, Integer price, Integer ticketNum)
    {
        //System.out.println("start executing m5");
        Integer ID = (Integer) runtime.invokeObj("customer", "getID", "m5", callerId+"::m5", ++n);
        runtime.invoke("m4", callerId+"::m4", ++n, price, ticketNum, ID);
    }

    public void m1(String callerId, Integer n, Integer price, Integer ticketNum)
    {
        //System.out.println("start executing m1");
        runtime.invokeObj("airline", "decSeat", "m1", callerId+"::m1", ++n, ticketNum);
        runtime.invoke("m0", callerId+"::m1", ++n, price);
    }
}
