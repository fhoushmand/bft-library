package bftsmart.usecase.ticket;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

public class TicketSystemClient extends PartitionedObject implements Client {
	@Override
	public void request(Object... args) { buyTicket(); }

	public void buyTicket()
	{
		runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
		runtime.invoke("m9", "buyTicket", sequenceNumber++);
	}

	public void ret(String callerId, Integer n, Boolean bought){
		String seqNumber = callerId.split("::")[1];
		int id = Integer.valueOf(seqNumber);
		runtime.getExecs().put(id, System.currentTimeMillis() - runtime.getExecs().get(id));
		System.out.println(String.format("response time for call %s: %s", id, runtime.getExecs().get(id)));
		responseReceived++;
		objCallLock.lock();
		requestBlock.signalAll();
		objCallLock.unlock();
	}
}