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
    public UserAgentClient userAgent;

    public void m3(String callerId, Integer n, TicketInfo info)
    {
        runtime.invokeObj("userAgent", "updateInfo", "m3", callerId+"::m3", ++n, info);
        runtime.invoke("m5", callerId+"::m3", ++n, info.price);

    }

//    public void m4(String callerId, Integer n, Integer price)
//    {
//        Integer user = (Integer) runtime.invokeObj("userAgent", "read", "m4", callerId+"::m4", ++n);
//        runtime.invoke("m5", callerId+"::m4", ++n, price);
//    }

    public void m6(String callerId, Integer n, IntIntPair cashbackBalance, Integer price)
    {
        runtime.invokeObj("userAgent", "updatePayment", "m6", callerId+"::m6", ++n, cashbackBalance);
        if(price <= cashbackBalance.getSecond())
            runtime.invoke("m7", callerId+"::m6", ++n, price);
        else
            runtime.invoke("ret", callerId+"::m6", ++n, Boolean.FALSE);
    }
}
