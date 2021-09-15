package bftsmart.usecase.onetimetransfer_optimized;

import bftsmart.demo.register.BooleanRegisterClient;
import bftsmart.demo.register.IntegerRegisterClient;
import bftsmart.usecase.PartitionedObject;
public class OTTA extends PartitionedObject {
	public IntegerRegisterClient i1;
	
	public void m0(String callerId, Integer n){
		logger.trace("execute m0");
		Integer x8 = (Integer) runtime.invokeObj("i1", "read", "m0", callerId+"::m0", ++n);
		runtime.invoke("ret", callerId+"::m0", ++n, x8);
	}
}