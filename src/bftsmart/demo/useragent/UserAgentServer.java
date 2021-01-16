package bftsmart.demo.useragent;

import bftsmart.demo.map.MapServer;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.usecase.auction.OfferInfo;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserAgentServer extends DefaultSingleRecoverable {

    private Logger logger;

    private int userID = 1;

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
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            UserAgentRequestType reqType = (UserAgentRequestType)objIn.readObject();

            //reading the id of the object call
            int idSize = objIn.readInt();
            byte[] idBytes = new byte[idSize];
            objIn.read(idBytes);
            String id = new String(idBytes);

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
                default:
                    logger.log(Level.WARNING, "in appExecuteOrdered only update offer is supported");
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
            logger.log(Level.SEVERE, "Occurred during useragent operation execution", e);
        }
        return reply;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
//        System.out.println("cache: " + cachedCalls);
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            UserAgentRequestType reqType = (UserAgentRequestType)objIn.readObject();

            //reading the id of the object call
            int idSize = objIn.readInt();
            byte[] idBytes = new byte[idSize];
            objIn.read(idBytes);
            String id = new String(idBytes);


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
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
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
