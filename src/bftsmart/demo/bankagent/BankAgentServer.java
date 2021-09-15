package bftsmart.demo.bankagent;

import bftsmart.demo.map.MapServer;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.usecase.auction.OfferInfo;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BankAgentServer extends DefaultSingleRecoverable {

    private Logger logger;

    private int userID = 1;

    int userBalance = 25000;
    int userCashback = 40;

    //IntIntPair userAccount = new IntIntPair(40,25000);

    HashMap<String,Object> cachedCalls = new HashMap<>();


    public BankAgentServer(int init, int id, int clusterId) {
        logger = Logger.getLogger(BankAgentServer.class.getName());
        new ServiceReplica(id, this, this, clusterId);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: demo.useragent.BankAgentServer <server id> <cluster id>");
            System.exit(-1);
        }
        new BankAgentServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }


    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            BankAgentRequestType reqType = (BankAgentRequestType)objIn.readObject();
            String id = (String) objIn.readObject();

            switch (reqType) {
                case DEC_BALANCE:
                    int price = objIn.readInt();
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to read in cache");
//                        IntIntPair bal = getBalance();
//                        objOut.writeObject(bal.getSecond());

                        //userAccount.setSecond(userAccount.getSecond()-price);
                        userBalance = userBalance - price;
                        cachedCalls.put(id, price);

//                        userBalance.setSecond(bal.getSecond()-price);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to read");
//                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case GET_BALANCE1:
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject(userCashback);
                        cachedCalls.put(id, userCashback);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to "+ cachedCalls.get(id));
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case GET_BALANCE2:
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject(userBalance);
                        cachedCalls.put(id, userBalance);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to "+ cachedCalls.get(id));
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
/*                case GET_BALANCE:
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject(userAccount);
                        cachedCalls.put(id, userAccount);
                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to "+ cachedCalls.get(id));
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;*/
                default:
                    logger.log(Level.WARNING, "bankagent: in appExecuteOrdered");
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
            logger.log(Level.SEVERE, "Occurred ordered operation execution", e);
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
            BankAgentRequestType reqType = (BankAgentRequestType)objIn.readObject();
            String id = (String) objIn.readObject();

            switch (reqType) {
                case GET_BALANCE1:
                    if(!cachedCalls.containsKey(id)) {
//                        IntIntPair bal = getBalance();
                        objOut.writeObject(userCashback);
//                        objOut.flush();
//                        byteOut.flush();
//                        reply = byteOut.toByteArray();
//
                        cachedCalls.put(id, userCashback);
//                        logger.log(Level.INFO, "putting id " + id + " call " + bal +  " read in cache");

                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to "+ cachedCalls.get(id));
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case GET_BALANCE2:
                    if(!cachedCalls.containsKey(id)) {
//                        IntIntPair bal = getBalance();
                        objOut.writeObject(userBalance);
//                        objOut.flush();
//                        byteOut.flush();
//                        reply = byteOut.toByteArray();
//
                        cachedCalls.put(id, userBalance);
//                        logger.log(Level.INFO, "putting id " + id + " call " + bal +  " read in cache");

                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to "+ cachedCalls.get(id));
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                /*case GET_BALANCE:
                    if(!cachedCalls.containsKey(id)) {
//                        IntIntPair bal = getBalance();
                        objOut.writeObject(userAccount);
//                        objOut.flush();
//                        byteOut.flush();
//                        reply = byteOut.toByteArray();
//
                        cachedCalls.put(id, userAccount);
//                        logger.log(Level.INFO, "putting id " + id + " call " + bal +  " read in cache");

                    }
                    else
                    {
//                        logger.log(Level.INFO, "cache hit with id " + id + " call to "+ cachedCalls.get(id));
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;*/
                default:
                    logger.log(Level.WARNING, "bankagent: in appExecuteunOrdered");
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
            logger.log(Level.SEVERE, "Occurred unordered operation execution", e);
        }
//        try (ByteArrayInputStream byteIn1 = new ByteArrayInputStream(reply);
//                             ObjectInput objIn1 = new ObjectInputStream(byteIn1)) {
//                            System.out.println(serverID + ":" +(IntIntPair)objIn1.readObject());
//        }
//        catch (Exception e){e.printStackTrace();}
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
