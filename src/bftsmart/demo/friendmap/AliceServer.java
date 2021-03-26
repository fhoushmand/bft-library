package bftsmart.demo.friendmap;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.hermes.runtime.HermesRuntime;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AliceServer extends DefaultSingleRecoverable {

    private Logger logger;
    private int userID = 1;


    HashMap<String,Object> cachedCalls = new HashMap<>();


    public AliceServer(int init, int id, int clusterId) {
        logger = Logger.getLogger(AliceServer.class.getName());
//        HermesRuntime.getInstance().setID(String.valueOf(id));
//        try {
//            HermesRuntime.getInstance().open();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        new ServiceReplica(id, this, this, clusterId);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: demo.useragent.UserAgentServer <server id> <cluster id>");
            System.exit(-1);
        }
        new AliceServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
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
            AliceRequestType reqType = (AliceRequestType)objIn.readObject();
            String id = (String) objIn.readObject();

            switch (reqType) {
                case EXPAND:
                    String box = (String) objIn.readObject();
                    String loc = (String) objIn.readObject();
                    String res = "||" + loc + "||";
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject(res);
                        cachedCalls.put(id, res);
                    }
                    else
                        objOut.writeObject(cachedCalls.get(id));
                    hasReply = true;
                    break;
                case PIN:
                    String map = (String) objIn.readObject();
                    String comment = (String) objIn.readObject();
                    String annotatedMap = "@" + map + "(" + comment + ")";
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject(annotatedMap);
                        cachedCalls.put(id, annotatedMap);
                    }
                    else
                        objOut.writeObject(cachedCalls.get(id));
                    hasReply = true;
                    break;
                case NEW_BOX:
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject("BOX");
                        cachedCalls.put(id, "BOX");
                    }
                    else
                        objOut.writeObject(cachedCalls.get(id));
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "aliceServer: in appExecuteOrdered");
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
            AliceRequestType reqType = (AliceRequestType)objIn.readObject();
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
                default:
                    logger.log(Level.WARNING, "alice server: in appExecuteUnOrdered");
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
