package bftsmart.usecase.ticket;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.bankagent.BankAgentClient;
import bftsmart.demo.useragent.UserAgentClient;
public class TicketSystemA extends PartitionedObject {
	public AirlineAgentClient airline;
	
	public void m1(String callerId, Integer n, Integer price, Integer num){
		logger.trace("execute m1");
		runtime.invokeObj("airline", "decSeat", "m1", callerId+"::m1", ++n, num);
		runtime.invoke("m0", callerId+"::m1", ++n, price);
	}
	public void m5(String callerId, Integer n, Integer price, Integer num){
		logger.trace("execute m5");
		Integer ID = (Integer) runtime.invokeObj("customer", "getID", "m5", callerId+"::m5", ++n);
		runtime.invoke("m4", callerId+"::m5", ++n, price, num, ID);
	}
	public void m6(String callerId, Integer n, String schedule, Integer price, Integer num){
		logger.trace("execute m6");
		runtime.invokeObj("customer", "updateInfo", "m6", callerId+"::m6", ++n, schedule, price);
		runtime.invoke("m5", callerId+"::m6", ++n, price, num);
	}
	public void m7(String callerId, Integer n, String schedule, Integer num){
		logger.trace("execute m7");
		Integer price = (Integer) runtime.invokeObj("airline", "getPrice2", "m7", callerId+"::m7", ++n, num);
		runtime.invoke("m6", callerId+"::m7", ++n, schedule, price, num);
	}
	public void m8(String callerId, Integer n, Integer num){
		logger.trace("execute m8");
		String schedule = (String) runtime.invokeObj("airline", "getPrice1", "m8", callerId+"::m8", ++n, num);
		runtime.invoke("m7", callerId+"::m8", ++n, schedule, num);
	}
	public void m9(String callerId, Integer n){
		logger.trace("execute m9");
		Integer num = (Integer) runtime.invokeObj("customer", "ticketNum", "m9", callerId+"::m9", ++n);
		runtime.invoke("m8", callerId+"::m9", ++n, num);
	}
}