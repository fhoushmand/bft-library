package bftsmart.demo.friendmap;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.hermes.runtime.HermesRuntime;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BobServer extends DefaultSingleRecoverable {

    private Logger logger;
    private int userID = 100;

    HashMap<String,Object> cachedCalls = new HashMap<>();

    public BobServer(int init, int id, int clusterId) {
        logger = Logger.getLogger(BobServer.class.getName());
        HermesRuntime.getInstance().setID(String.valueOf(id));
        try {
            HermesRuntime.getInstance().open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new ServiceReplica(id, this, this, clusterId);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: demo.useragent.UserAgentServer <server id> <cluster id>");
            System.exit(-1);
        }
        new BobServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
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
            BobRequestType reqType = (BobRequestType)objIn.readObject();
            String id = (String) objIn.readObject();


            switch (reqType) {
                case COMMENT:
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject("bob comment");
                        cachedCalls.put(id, "bob comment");
                    }
                    else
                        objOut.writeObject(cachedCalls.get(id));
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "bobServer: in appExecuteOrdered");
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
            BobRequestType reqType = (BobRequestType)objIn.readObject();
            String id = (String) objIn.readObject();


            switch (reqType) {
                case ID:
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject(userID);
                        cachedCalls.put(id, userID);
                    }
                    else
                        objOut.writeObject(cachedCalls.get(id));
                    hasReply = true;
                    break;
                case LOCATION:
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject("1@2@3");
                        cachedCalls.put(id, "1@2@3");
                    }
                    else
                        objOut.writeObject(cachedCalls.get(id));
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "bob server: in appExecuteUnOrdered");
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
            objOut.writeObject(cachedCalls);
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
            cachedCalls = (HashMap<String, Object>) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error while installing snapshot", e);
        }
    }
}
