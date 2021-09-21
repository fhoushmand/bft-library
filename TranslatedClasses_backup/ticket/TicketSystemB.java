package bftsmart.usecase.ticket;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.bankagent.BankAgentClient;
import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.demo.friendmap.MapServiceClient;
public class TicketSystemB extends PartitionedObject {
	public BankAgentClient bank;
	
	public void m0(String callerId, Integer n, Integer price){
		logger.trace("execute m0");
		runtime.invokeObj("bank", "decBalance", "m0", callerId+"::m0", ++n, price);
		runtime.invoke("ret", callerId+"::m0", ++n, true);
	}
	public void m2(String callerId, Integer n, Integer balance, Integer price, Integer num, Integer cashback){
		logger.trace("execute m2");
		runtime.invokeObj("customer", "updatePayment", "m2", callerId+"::m2", ++n, cashback, balance);
		if(price <= balance){
			runtime.invoke("m1", callerId+"::m2", ++n, price, num);
		}
		else{
			runtime.invoke("ret", callerId+"::m2", ++n, false);
		}
	}
	public void m3(String callerId, Integer n, Integer price, Integer num, Integer ID, Integer cashback){
		logger.trace("execute m3");
		Integer balance = (Integer) runtime.invokeObj("bank", "getBalance2", "m3", callerId+"::m3", ++n, ID);
		runtime.invoke("m2", callerId+"::m3", ++n, balance, price, num, cashback);
	}
	public void m4(String callerId, Integer n, Integer price, Integer num, Integer ID){
		logger.trace("execute m4");
		Integer cashback = (Integer) runtime.invokeObj("bank", "getBalance1", "m4", callerId+"::m4", ++n, ID);
		runtime.invoke("m3", callerId+"::m4", ++n, price, num, ID, cashback);
	}
}