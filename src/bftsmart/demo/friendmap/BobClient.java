package bftsmart.demo.friendmap;

import bftsmart.tom.ServiceProxy;

import java.io.*;

public class BobClient {

	public ServiceProxy serviceProxy;

	public BobClient(Integer clientId, Integer clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public Integer getID(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(BobRequestType.ID);

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
			System.out.println("Exception get id in bob: " + e.getMessage());
		}
		return null;
	}

	public String location(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(BobRequestType.LOCATION);

			objOut.writeObject(id);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (String) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception get location in bob: " + e.getMessage());
		}
		return null;
	}

	public String comment(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(BobRequestType.COMMENT);

			objOut.writeObject(id);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (String) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception comment bob client: " + e.getMessage());
		}
		return null;
	}
}