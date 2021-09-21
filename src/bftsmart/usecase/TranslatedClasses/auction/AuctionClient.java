package bftsmart.usecase.auction;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

public class AuctionClient extends PartitionedObject implements Client {
	@Override
	public void request(Object... args) { auction((Integer) arg[0]); }

	public void auction(Integer o)
	{
		runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
		runtime.invoke("m8", "auction", sequenceNumber++, o);
	}

	public void ret(String callerId, Integer n, OfferInfo offer){
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