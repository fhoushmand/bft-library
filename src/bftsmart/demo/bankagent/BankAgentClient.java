package bftsmart.demo.bankagent;

import bftsmart.runtime.util.IntIntPair;
import bftsmart.tom.ServiceProxy;

import java.io.*;

public class BankAgentClient {

	public ServiceProxy serviceProxy;

	public BankAgentClient(Integer clientId, Integer clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

/*	public IntIntPair getBalance(Integer ID, String id)
	{
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(BankAgentRequestType.GET_BALANCE);
			objOut.writeObject(id);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (IntIntPair) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Exception getting the balance of the user in bank agent: " + e.getMessage());
		}
		return null;
	}*/

	public Integer getBalance1(Integer ID, String id)
	{
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(BankAgentRequestType.GET_BALANCE1);
			objOut.writeObject(id);

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
			e.printStackTrace();
			System.out.println("Exception getting the cashback of the user in bank agent: " + e.getMessage());
		}
		return null;
	}

	public Integer getBalance2(Integer ID, String id)
	{
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(BankAgentRequestType.GET_BALANCE2);
			objOut.writeObject(id);

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
			e.printStackTrace();
			System.out.println("Exception getting the balance of the user in bank agent: " + e.getMessage());
		}
		return null;
	}


	public void decBalance(Integer price, String id)
	{
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(BankAgentRequestType.DEC_BALANCE);
			objOut.writeObject(id);
			objOut.writeInt(price);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply.length == 0)
				return;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return;
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Exception getting the price of the ticket in bank ag: " + e.getMessage());
		}
		return;
	}

}