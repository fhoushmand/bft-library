package bftsmart.demo.AirlineAgent;

import bftsmart.tom.ServiceProxy;
import bftsmart.usecase.auction.OfferInfo;

import java.io.*;

public class AirlineAgentClient {

	public ServiceProxy serviceProxy;

	public AirlineAgentClient(int clientId, int clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public OfferInfo makeOfferA(Integer user, Integer offer, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AirlineAgentRequestType.MAKE_OFFER_A);
			objOut.writeObject(id);
			objOut.writeInt(user);
			objOut.writeInt(offer);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (OfferInfo) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Exception making offer in airlineA: " + e.getMessage());
		}
		return null;
	}

	public OfferInfo makeOfferB(Integer user, Integer offer, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AirlineAgentRequestType.MAKE_OFFER_B);
			objOut.writeObject(id);
			objOut.writeInt(user);
			objOut.writeInt(offer);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (OfferInfo) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Exception making offer in airlineB: " + e.getMessage());
		}
		return null;
	}
}