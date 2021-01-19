package bftsmart.usecase.ticket;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

public class TicketSystemA extends PartitionedObject {

    public AirlineAgentClient airlineAgent;

    public TicketSystemA(HashMap<Integer, String> hostipMap, String configuration) {
        super(hostipMap, configuration);
    }

    public void m2(String callerId, Integer n, Integer ticketNum)
    {
        TicketInfo ticketInfo = (TicketInfo) runtime.invokeObj("airlineAgent", "getPrice", "m2", callerId+"::m2", ++n, ticketNum);
        runtime.invoke("m3", callerId+"::m2", ++n, ticketInfo);
    }

    public void m7(String callerId, Integer n, Integer price)
    {
        runtime.invokeObj("airlineAgent", "decSeat", "m7", callerId+"::m7", ++n);
        runtime.invoke("m8", callerId+"::m7", ++n, price);

    }
}
