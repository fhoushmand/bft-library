package bftsmart.usecase.friendmap;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

public class FriendMapClient extends PartitionedObject implements Client {
	@Override
	public void request(Object... args) { friendMap(); }

	public void friendMap()
	{
		runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
		runtime.invoke("m8", "friendMap", sequenceNumber++);
	}

	public void ret(String callerId, Integer n, String map){
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