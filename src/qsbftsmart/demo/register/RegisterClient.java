package qsbftsmart.demo.register;

import qsbftsmart.tom.ServiceProxy;

import java.io.*;

public class RegisterClient<T>{

	ServiceProxy serviceProxy;

	public RegisterClient(int clientId) {
		serviceProxy = new ServiceProxy(clientId);
	}

	public T write(T newVal) {
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
				return (T)objIn.readObject();
			}
				
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception writing value into register: " + e.getMessage());
		}
		return null;
	}

	public T read() {
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
			 	return (T)objIn.readObject();
			 }
				
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception getting value from register: " + e.getMessage());
		}
		return null;
	}
}