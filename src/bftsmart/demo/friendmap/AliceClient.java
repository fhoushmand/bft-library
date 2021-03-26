package bftsmart.demo.friendmap;

import bftsmart.tom.ServiceProxy;

import java.io.*;

public class AliceClient {

	public ServiceProxy serviceProxy;

	public AliceClient(Integer clientId, Integer clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public Integer getID(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AliceRequestType.ID);

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
			System.out.println("Exception get id in alice");
		}
		return null;
	}

	public String pin(String map, String comment, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AliceRequestType.PIN);

			objOut.writeObject(id);

			objOut.writeObject(map);
			objOut.writeObject(comment);

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
			System.out.println("Exception pin map in alice client: " + e.getMessage());
		}
		return null;
	}

	public String newBox(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AliceRequestType.NEW_BOX);

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
			System.out.println("Exception new box in alice client: " + e.getMessage());
		}
		return null;
	}

	public String expand(String box, String loc, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AliceRequestType.EXPAND);

			objOut.writeObject(id);

			objOut.writeObject(box);
			objOut.writeObject(loc);

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
			System.out.println("Exception expand map in alice client: " + e.getMessage());
		}
		return null;
	}
}