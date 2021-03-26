package bftsmart.demo.mpc;

import bftsmart.runtime.util.IntIntIntTuple;
import bftsmart.tom.ServiceProxy;

import java.io.*;

public class MPCClient {

	public ServiceProxy serviceProxy;

	public MPCClient(Integer clientId, Integer clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public IntIntIntTuple split(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut);) {

			objOut.writeObject(MPCRequestType.SPLIT);

			objOut.writeInt(id.getBytes().length);
			objOut.write(id.getBytes());

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (IntIntIntTuple) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception splitting value: " + e.getMessage());
		}
		return null;
	}

	public Integer read(Integer pieceNum, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(MPCRequestType.READ);

			objOut.writeInt(id.getBytes().length);
			objOut.write(id.getBytes());

			objOut.writeInt(pieceNum);

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
			System.out.println("Exception getting value from mpc: " + e.getMessage());
		}
		return null;
	}
}