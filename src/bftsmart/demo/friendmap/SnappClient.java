package bftsmart.demo.friendmap;

import bftsmart.tom.ServiceProxy;

import java.io.*;

public class SnappClient {

	public ServiceProxy serviceProxy;

	public SnappClient(Integer clientId, Integer clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public Boolean isFriend(Integer aID, Integer bID, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(SnappRequestType.IS_FRIEND);

			objOut.writeObject(id);

			objOut.writeInt(aID);
			objOut.writeInt(bID);

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
			System.out.println("Exception get is friend in snapp client: " + e.getMessage());
		}
		return null;
	}
}