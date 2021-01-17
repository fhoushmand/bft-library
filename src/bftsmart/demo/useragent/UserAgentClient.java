package bftsmart.demo.useragent;

import bftsmart.tom.ServiceProxy;
import bftsmart.usecase.auction.OfferInfo;

import java.io.*;
import java.util.Scanner;

public class UserAgentClient{

	public ServiceProxy serviceProxy;

	public UserAgentClient(int clientId, int clusterId)
	{
		serviceProxy = new ServiceProxy(clientId, clusterId);
	}

	public Integer read(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut);) {

			objOut.writeObject(UserAgentRequestType.READ);
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
			System.out.println("Exception reading value from userAgent: " + e.getMessage());
		}
		return null;
	}

	public void updateOffer(OfferInfo offer, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.UPDATE_OFFER);
			objOut.writeObject(id);
			objOut.writeObject(offer);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply == null || reply.length == 0)
				return;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return;
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Exception updating the offers in userAgent: " + e.getMessage());
		}
		return;
	}

	public OfferInfo declareWinner(Integer offer, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.DECLARE_WINNER);
			objOut.writeObject(id);
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
			System.out.println("Exception declaring winner in userAgent: " + e.getMessage());
		}
		return null;
	}
}