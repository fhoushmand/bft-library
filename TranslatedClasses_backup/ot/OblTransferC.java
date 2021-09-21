package bftsmart.usecase.obltransfer;

import bftsmart.demo.register.BooleanRegisterClient;
import bftsmart.demo.register.IntegerRegisterClient;
import bftsmart.usecase.PartitionedObject;
public class OblTransferC extends PartitionedObject {
	
	public void m0(String callerId, Integer n, Integer x, Integer temp1){
		logger.trace("execute m0");
		Integer temp2 = (Integer) runtime.invokeObj("i2", "read", "m0", callerId+"::m0", ++n);
		if(x == 1){
			runtime.invoke("ret", callerId+"::m0", ++n, temp1);
		}
		else{
			runtime.invoke("ret", callerId+"::m0", ++n, temp2);
		}
	}
	public void m1(String callerId, Integer n, Integer x){
		logger.trace("execute m1");
		Integer temp1 = (Integer) runtime.invokeObj("i1", "read", "m1", callerId+"::m1", ++n);
		runtime.invoke("m0", callerId+"::m1", ++n, x, temp1);
	}
	public void m2(String callerId, Integer n, Integer x){
		logger.trace("execute m2");
		Boolean x12 = (Boolean) runtime.invokeObj("a", "write", "m2", callerId+"::m2", ++n, true);
		runtime.invoke("m1", callerId+"::m2", ++n, x);
	}
	public void m3(String callerId, Integer n, Integer x){
		logger.trace("execute m3");
		Boolean x14 = (Boolean) runtime.invokeObj("a", "read", "m3", callerId+"::m3", ++n);
		if(x14){
			runtime.invoke("m2", callerId+"::m3", ++n, x);
		}
		else{
			runtime.invoke("ret", callerId+"::m3", ++n, 0);
		}
	}
}