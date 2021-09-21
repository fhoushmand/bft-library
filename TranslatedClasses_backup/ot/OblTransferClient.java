package bftsmart.usecase.obltransfer;

import bftsmart.usecase.Client;
import bftsmart.usecase.PartitionedObject;

public class OblTransferClient extends PartitionedObject implements Client {
	@Override
	public void request(Object... args) { transfer((Integer) arg[0]); }

	public void transfer(Integer x)
	{
		runtime.getExecs().put(sequenceNumber, System.currentTimeMillis());
		runtime.invoke("m3", "transfer", sequenceNumber++, x);
	}

	public void ret(String callerId, Integer n, Integer x){
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