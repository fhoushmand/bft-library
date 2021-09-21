package bftsmart.usecase.ticket;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.usecase.PartitionedObject;
import bftsmart.demo.bankagent.BankAgentClient;
import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.demo.friendmap.MapServiceClient;
public class TicketSystemC extends PartitionedObject {
	public UserAgentClient customer;
	
}