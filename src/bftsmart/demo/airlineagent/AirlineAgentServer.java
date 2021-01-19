package bftsmart.demo.airlineagent;

import bftsmart.demo.map.MapServer;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.usecase.auction.OfferInfo;
import bftsmart.usecase.ticket.TicketInfo;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AirlineAgentServer extends DefaultSingleRecoverable {

    private Logger logger;

    private int userID = 1;

    int ABestOffer = 300;
    int BBestOffer = 350;

    HashMap<String,Object> cachedCalls = new HashMap<>();

    HashMap<Integer,TicketInfo> tickets = new HashMap<>();

    int availableSeats = 100;
    TicketInfo price = new TicketInfo("ticket", 50);


    public AirlineAgentServer(int init, int id, int clusterId) {
        logger = Logger.getLogger(AirlineAgentServer.class.getName());
        for (int i = 0; i < 100; i++)
            tickets.put(i, new TicketInfo("ticket", new Random().nextInt(500)));
        new ServiceReplica(id, this, this, clusterId);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: demo.useragent.UserAgentServer <server id> <cluster id>");
            System.exit(-1);
        }
        new AirlineAgentServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    public OfferInfo makeOfferA(Integer user, Integer offer)
    {
        if(offer > ABestOffer )
            return new OfferInfo("airlineA", "seatInfoA", offer-1);
        return new OfferInfo("airlineA", "seatInfoA", ABestOffer);
    }

    public OfferInfo makeOfferB(Integer user, Integer offer)
    {
        if(offer > BBestOffer)
            return new OfferInfo("airlineB", "seatInfoB", offer-1);
        return new OfferInfo("airlineB", "seatInfoB", BBestOffer);
    }


    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
//        System.err.println("ordered call in airline agent. probably due to failed synch.");
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            AirlineAgentRequestType reqType = (AirlineAgentRequestType)objIn.readObject();
            String id = (String) objIn.readObject();


            switch (reqType) {
                case MAKE_OFFER_A:
                    int u = objIn.readInt();
                    int o = objIn.readInt();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        OfferInfo out = makeOfferA(u, o);
                        objOut.writeObject(out);
                        cachedCalls.put(id, out);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case MAKE_OFFER_B:
                    u = objIn.readInt();
                    o = objIn.readInt();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        OfferInfo out = makeOfferB(u, o);
                        objOut.writeObject(out);
                        cachedCalls.put(id, out);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case DEC_SEAT:
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        objOut.writeObject(availableSeats);
                        cachedCalls.put(id, availableSeats);
                        availableSeats--;
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "airlineAgent: in appExecuteOrdered");
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
            logger.log(Level.SEVERE, "Occurred during ordered execution", e);
        }
        return reply;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            AirlineAgentRequestType reqType = (AirlineAgentRequestType)objIn.readObject();
            String id = (String) objIn.readObject();


            switch (reqType) {
                case MAKE_OFFER_A:
                    int u = objIn.readInt();
                    int o = objIn.readInt();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        OfferInfo out = makeOfferA(u, o);
                        objOut.writeObject(out);
                        cachedCalls.put(id, out);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case MAKE_OFFER_B:
                    u = objIn.readInt();
                    o = objIn.readInt();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        OfferInfo out = makeOfferB(u, o);
                        objOut.writeObject(out);
                        cachedCalls.put(id, out);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case GET_PRICE:
                    int ticketNum = objIn.readInt();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
                        objOut.writeObject(price);
                        cachedCalls.put(id, price);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "airlineAgent: in appExecuteununOrdered");
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
            logger.log(Level.SEVERE, "Occurred during unordered execution", e);
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
