package bftsmart.demo.register;

import bftsmart.tom.ServiceProxy;

import java.io.*;
import java.util.Scanner;

public class MapServiceClient {

	public ServiceProxy serviceProxy;

	public MapServiceClient(Integer clientId, Integer clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public String pin(String box, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(MapServiceRequestType.PIN);

			objOut.writeInt(id.getBytes().length);
			objOut.write(id.getBytes());

			objOut.writeInt(box.getBytes().length);
			objOut.write(box.getBytes());

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
			System.out.println("Exception pin map in map client: " + e.getMessage());
		}
		return null;
	}

	public String newBox(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(MapServiceRequestType.NEW_BOX);

			objOut.writeInt(id.getBytes().length);
			objOut.write(id.getBytes());

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
			System.out.println("Exception new box in map client: " + e.getMessage());
		}
		return null;
	}

	public String extend(String box, String loc, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(MapServiceRequestType.EXTEND);

			objOut.writeInt(id.getBytes().length);
			objOut.write(id.getBytes());

			objOut.writeInt(box.getBytes().length);
			objOut.write(box.getBytes());

			objOut.writeInt(loc.getBytes().length);
			objOut.write(loc.getBytes());

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
			System.out.println("Exception pin map in map client: " + e.getMessage());
		}
		return null;
	}
}