package bftsmart.demo.useragent;

import bftsmart.demo.map.MapServer;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.usecase.auction.OfferInfo;
import bftsmart.usecase.ticket.TicketInfo;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserAgentServer extends DefaultSingleRecoverable {

    private Logger logger;

    private int userID = 1;
    private int ticketNumber = 10;

    TicketInfo ticket;
    IntIntPair cashbackBalance;

    HashMap<String,Object> cachedCalls = new HashMap<>();

    HashMap<String, OfferInfo> airlineOffers = new HashMap<>();


    public UserAgentServer(int init, int id, int clusterId) {
        logger = Logger.getLogger(MapServer.class.getName());
        new ServiceReplica(id, this, this, clusterId);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: demo.useragent.UserAgentServer <server id> <cluster id>");
            System.exit(-1);
        }
        new UserAgentServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    public int updateOffer(OfferInfo offerInfo)
    {
        airlineOffers.put(offerInfo.airlineName, offerInfo);
        return 1;
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        byte[] reply = null;
        boolean hasReply = false;
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            UserAgentRequestType reqType = (UserAgentRequestType)objIn.readObject();
            String id = (String) objIn.readObject();

            // Only do the operation (marked) if the call is not already executed
            switch (reqType) {
                case UPDATE_OFFER:
                    OfferInfo o = (OfferInfo) objIn.readObject();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        int out = updateOffer(o);
//                        objOut.writeObject(out);
                        cachedCalls.put(id, out);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
//                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case UPDATE_INFO:
                    TicketInfo ticketInfo = (TicketInfo) objIn.readObject();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        cachedCalls.put(id, ticket);
                        ticket = ticketInfo;
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
//                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case UPDATE_PAYEMENT:
                    IntIntPair cashbackBalance = (IntIntPair) objIn.readObject();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        cachedCalls.put(id, cashbackBalance);
                        this.cashbackBalance = cashbackBalance;
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                    }
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "in appExecuteOrdered is supported");
            }
            if (hasReply) {
                objOut.flush();
                byteOut.flush();
                reply = byteOut.toByteArray();
            } else {
                reply = new byte[0];
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
//            System.out.println("size of id: " + idSize);
//            System.out.println("id: " + id);
            logger.log(Level.SEVERE, "Occurred during useragent operation execution", e);
        }
//        finally {
//            System.out.println("size of id: " + idSize);
//            System.out.println("id: " + id);
//        }
        return reply;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
//        System.out.println("cache: " + cachedCalls);
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            UserAgentRequestType reqType = (UserAgentRequestType)objIn.readObject();
            String id = (String) objIn.readObject();

            switch (reqType) {
                case READ:
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        objOut.writeObject(userID);
                        cachedCalls.put(id, userID);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case DECLARE_WINNER:
                    int winningOffer = objIn.readInt();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        OfferInfo winnigOfferInfo = (airlineOffers.get("airlineA").offer == winningOffer) ? airlineOffers.get("airlineA") : airlineOffers.get("airlineB");
                        objOut.writeObject(winnigOfferInfo);
                        cachedCalls.put(id, userID);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case TICKET_NUM:
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        objOut.writeObject(ticketNumber);
                        cachedCalls.put(id, ticketNumber);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case GET_ID:
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        objOut.writeObject(ticketNumber);
                        cachedCalls.put(id, ticketNumber);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;

                default:
                    logger.log(Level.WARNING, "in appExecuteOrdered only read operations are supported");
            }
            if (hasReply) {
                objOut.flush();
                byteOut.flush();
                reply = byteOut.toByteArray();
            } else {
                reply = new byte[0];
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Occurred during register operation execution", e);
        }
        return reply;
    }

    @Override
    public byte[] getSnapshot() {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
            objOut.writeObject(userID);
            return byteOut.toByteArray();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while taking snapshot", e);
        }
        return new byte[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public void installSnapshot(byte[] state) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(state);
             ObjectInput objIn = new ObjectInputStream(byteIn)) {
            userID = (int) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error while installing snapshot", e);
        }
    }
}
