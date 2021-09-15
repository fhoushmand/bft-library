package bftsmart.demo.airlineagent;

import bftsmart.tom.ServiceProxy;
import bftsmart.usecase.auction.OfferInfo;
//import bftsmart.usecase.ticket.TicketInfo;

import java.io.*;

public class AirlineAgentClient {

	public ServiceProxy serviceProxy;

	public AirlineAgentClient(Integer clientId, Integer clusterId)
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
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
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

	/*public TicketInfo getPrice(Integer ticket, String id)
	{
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AirlineAgentRequestType.GET_PRICE);
			objOut.writeObject(id);
			objOut.writeInt(ticket);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
			if (reply.length == 0)
				return null;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return (TicketInfo) objIn.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Exception getting the price of the ticket in airlineAgentClient: " + e.getMessage());
		}
		return null;
	}*/

	public String getPrice1(Integer ticket, String id)
	{
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AirlineAgentRequestType.GET_PRICE1);
			objOut.writeObject(id);
			objOut.writeInt(ticket);

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
			e.printStackTrace();
			System.out.println("Exception getting the schedule of the ticket in airlineAgentClient: " + e.getMessage());
		}
		return null;
	}

	public Integer getPrice2(Integer ticket, String id)
	{
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AirlineAgentRequestType.GET_PRICE2);
			objOut.writeObject(id);
			objOut.writeInt(ticket);

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
			System.out.println("Exception getting the price of the ticket in airlineAgentClient: " + e.getMessage());
		}
		return null;
	}


	public void decSeat(Integer ticketNum, String id)
	{
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(AirlineAgentRequestType.DEC_SEAT);
			objOut.writeObject(id);
			objOut.writeInt(ticketNum);

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
			System.out.println("Exception dec seat in airlineAgentClient: " + e.getMessage());
		}
		return;
	}

}