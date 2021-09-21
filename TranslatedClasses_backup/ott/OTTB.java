package bftsmart.usecase.onetimetransfer_optimized;

import bftsmart.demo.register.BooleanRegisterClient;
import bftsmart.demo.register.IntegerRegisterClient;
import bftsmart.usecase.PartitionedObject;
public class OTTB extends PartitionedObject {
	public BooleanRegisterClient a;
	public IntegerRegisterClient i2;
	
	public void m1(String callerId, Integer n){
		logger.trace("execute m1");
		Integer x9 = (Integer) runtime.invokeObj("i2", "read", "m1", callerId+"::m1", ++n);
		runtime.invoke("ret", callerId+"::m1", ++n, x9);
	}
	public void m2(String callerId, Integer n, Integer x){
		logger.trace("execute m2");
		Boolean x10 = (Boolean) runtime.invokeObj("a", "write", "m2", callerId+"::m2", ++n, true);
		if(x == 1){
			runtime.invoke("m0", callerId+"::m2", ++n);
		}
		else{
			runtime.invoke("m1", callerId+"::m2", ++n);
		}
	}
	public void m3(String callerId, Integer n, Integer x){
		logger.trace("execute m3");
		Boolean x12 = (Boolean) runtime.invokeObj("a", "read", "m3", callerId+"::m3", ++n);
		if(x12){
			runtime.invoke("m2", callerId+"::m3", ++n, x);
		}
		else{
			runtime.invoke("ret", callerId+"::m3", ++n, 0);
		}
	}
}