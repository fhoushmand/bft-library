package bftsmart.demo.register;

import bftsmart.tom.ServiceProxy;

import java.io.*;
import java.util.Scanner;

public class BooleanRegisterClient2 {

	ServiceProxy serviceProxy;

	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		BooleanRegisterClient2 client = new BooleanRegisterClient2(1, 4);
		int id = 0;
		while (in.hasNext())
		{
			String next = in.nextLine();
			if(next.equals("exit"))
				break;
			System.out.println("old: " + client.read(String.valueOf(++id)));
			client.write(Boolean.parseBoolean(next),String.valueOf(++id));
			System.out.println("new: " + client.read(String.valueOf(++id)));

		}
	}


	public BooleanRegisterClient2(int clientId, int clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public Boolean write(Boolean newVal, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
			
			objOut.writeObject(RegisterRequestType.WRITE);

			objOut.writeInt(id.getBytes().length);
			objOut.write(id.getBytes());

			objOut.writeObject(newVal);

			objOut.flush();
			byteOut.flush();
			
			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
					ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (Boolean) objIn.readObject();
			}
				
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception writing value into register: " + e.getMessage());
		}
		return null;
	}

	public Boolean read(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
			
			 objOut.writeObject(RegisterRequestType.READ);

			 objOut.writeInt(id.getBytes().length);
			 objOut.write(id.getBytes());
			
		 	 objOut.flush();
			 byteOut.flush();
			
			 byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
			 if (reply.length == 0)
			 	 return null;
			 try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				  ObjectInput objIn = new ObjectInputStream(byteIn)) {
			 	return (Boolean) objIn.readObject();
			 }
				
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception getting value from register: " + e.getMessage());
		}
		return null;
	}
}