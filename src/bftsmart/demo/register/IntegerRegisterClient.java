package bftsmart.demo.register;

import bftsmart.tom.ServiceProxy;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class IntegerRegisterClient{

	public ServiceProxy serviceProxy;

	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		IntegerRegisterClient client = new IntegerRegisterClient(0, 4);
		int id = 0;
		while (in.hasNext())
		{
			String next = in.nextLine();
			if(next.equals("exit"))
				break;
			System.out.println("old: " + client.read(String.valueOf(++id)));
			client.write(Integer.parseInt(next),String.valueOf(++id));
			System.out.println("new: " + client.read(String.valueOf(++id)));
		}
	}


	public IntegerRegisterClient(Integer clientId, Integer clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public Integer write(Integer newVal, String id) {
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
				return (Integer) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception writing value into register: " + e.getMessage());
		}
		return null;
	}

	public Integer read(String id) {
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
				return (Integer) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception getting value from register: " + e.getMessage());
		}
		return null;
	}
}