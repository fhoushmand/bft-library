package bftsmart.demo.friendmap;

import bftsmart.tom.ServiceProxy;

import java.io.*;

public class MapServiceClient {

	public ServiceProxy serviceProxy;

	public MapServiceClient(Integer clientId, Integer clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}


	public String getMap(String box, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(id);

			objOut.writeObject(box);

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
			System.out.println("Exception in mapClient in getMap: " + e.getMessage());
		}
		return null;
	}
}