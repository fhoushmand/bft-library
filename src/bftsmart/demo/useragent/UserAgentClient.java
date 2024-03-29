package bftsmart.demo.useragent;

import bftsmart.runtime.util.IntIntPair;
import bftsmart.tom.ServiceProxy;
import bftsmart.usecase.auction.OfferInfo;
import bftsmart.usecase.ticket.TicketInfo;

import java.io.*;
import java.util.Scanner;

public class UserAgentClient{

	public ServiceProxy serviceProxy;

	public UserAgentClient(Integer clientId, Integer clusterId)
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

	public Integer ticketNum(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.TICKET_NUM);
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

	public Integer getID(String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.GET_ID);
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

	public void update(OfferInfo seatInfo, Integer offer, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.UPDATE_OFFER);
			objOut.writeObject(id);
			objOut.writeObject(seatInfo);
			objOut.writeInt(offer);

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

	public void updateInfo(String schedule, Integer price, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.UPDATE_INFO);
			objOut.writeObject(id);
			TicketInfo tInfo = new TicketInfo(schedule, price);
			objOut.writeObject(tInfo);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply.length == 0)
				return ;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return ;
			}

		} catch (IOException e) {
			System.out.println("Exception updating info in userAgent: " + e.getMessage());
		}
		return ;
	}

	public void updatePayment(Integer cashback, Integer balance, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.UPDATE_PAYEMENT);
			objOut.writeObject(id);
			IntIntPair cb = new IntIntPair(cashback, balance);
			objOut.writeObject(cb);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply.length == 0)
				return ;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return ;
			}

		} catch (IOException e) {
			System.out.println("Exception updating payment in userAgent: " + e.getMessage());
		}
		return ;
	}

/*	public void updateInfo(TicketInfo ticketInfo, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.UPDATE_INFO);
			objOut.writeObject(id);
			objOut.writeObject(ticketInfo);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply.length == 0)
				return ;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return ;
			}

		} catch (IOException e) {
			System.out.println("Exception updating info in userAgent: " + e.getMessage());
		}
		return ;
	}

	public void updatePayment(IntIntPair cashbackBalance, String id) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

			objOut.writeObject(UserAgentRequestType.UPDATE_PAYEMENT);
			objOut.writeObject(id);
			objOut.writeObject(cashbackBalance);

			objOut.flush();
			byteOut.flush();

			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
			if (reply.length == 0)
				return ;
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
				 ObjectInput objIn = new ObjectInputStream(byteIn)) {
				return ;
			}

		} catch (IOException e) {
			System.out.println("Exception updating payement in userAgent: " + e.getMessage());
		}
		return ;
	}*/

}