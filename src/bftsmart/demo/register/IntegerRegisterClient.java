package bftsmart.demo.register;

import bftsmart.tom.ServiceProxy;

import java.io.*;
import java.util.HashMap;

public class IntegerRegisterClient {

	ServiceProxy serviceProxy;

	HashMap<String,Integer> cachedInvocations;


	public IntegerRegisterClient(int clientId, int clusterId)
	{
		cachedInvocations = new HashMap<>();
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public Integer write(Boolean newVal) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
			
			objOut.writeObject(RegisterRequestType.WRITE);
			objOut.writeObject(newVal);
			objOut.flush();
			byteOut.flush();
			
			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
					ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (Integer) objIn.readObject();
			}
				
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception writing value into register: " + e.getMessage());
		}
		return null;
	}

	public Integer read() {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
			
			 objOut.writeObject(RegisterRequestType.READ);
			
		 	 objOut.flush();
			 byteOut.flush();
			
			 byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
			 if (reply.length == 0)
			 	 return null;
			 try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				  ObjectInput objIn = new ObjectInputStream(byteIn)) {
			 	return (Integer) objIn.readObject();
			 }
				
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception getting value from register: " + e.getMessage());
		}
		return null;
	}
}