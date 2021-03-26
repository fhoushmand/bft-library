package bftsmart.demo.friendmap;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.hermes.runtime.HermesRuntime;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SnappServer extends DefaultSingleRecoverable {

    private Logger logger;
    HashMap<String,Object> cachedCalls = new HashMap<>();
    
    public SnappServer(int init, int id, int clusterId) {
        logger = Logger.getLogger(SnappServer.class.getName());
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
        new SnappServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        throw  new RuntimeException("not supported in snapp server");
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            SnappRequestType reqType = (SnappRequestType)objIn.readObject();
            String id = (String) objIn.readObject();
            int aID = objIn.readInt();
            int bID = objIn.readInt();

            switch (reqType) {
                case IS_FRIEND:
                    if(!cachedCalls.containsKey(id)) {
                        objOut.writeObject(aID == 1 && bID == 100);
                        cachedCalls.put(id, aID == 1 && bID == 100);
                    }
                    else
                        objOut.writeObject(cachedCalls.get(id));
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "snapp server: in appExecuteUnOrdered");
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
