package bftsmart.usecase.ticket;

import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.usecase.PartitionedObject;

import java.util.HashMap;

/**
 * m7 and m8 should be hosted on As and Bs respectively
 * where does the num (argument of m7 in m6) come from?
 * is it a fixed number?
 */
public class TicketSystemC extends PartitionedObject {
    public UserAgentClient customer;

//    public void m4(String callerId, Integer n, Integer price)
//    {
//        Integer user = (Integer) runtime.invokeObj("userAgent", "read", "m4", callerId+"::m4", ++n);
//        runtime.invoke("m5", callerId+"::m4", ++n, price);
//    }
}
