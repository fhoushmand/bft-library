package bftsmart.demo.mpc;

import bftsmart.demo.map.MapServer;
import bftsmart.runtime.util.IntIntIntTuple;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MPCServer extends DefaultSingleRecoverable {

    private int memory;
    private Logger logger;
    private IntIntIntTuple splittedSecret;

    HashMap<String,Object> cachedCalls = new HashMap<>();


    public MPCServer(int init, int id, int clusterId) {
        memory = init;
        splittedSecret = getSplitSecret(memory);
        System.out.println(splittedSecret);
        logger = Logger.getLogger(MapServer.class.getName());
        new ServiceReplica(id, this, this, clusterId);
    }

    private IntIntIntTuple getSplitSecret(int secret)
    {
//        int p1 = new Random().nextInt(secret) + 1;
//        int p2 = new Random().nextInt(secret - p1) + 1;
//        int p3 = secret - (p1+p2);
        int p1 = 100;
        int p2 = 80;
        int p3 = secret - (p1+p2);
        return new IntIntIntTuple(p1,p2,p3);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: demo.demo.MPCServer <default> <server id> <cluster id>");
            System.exit(-1);
        }
        new MPCServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            MPCRequestType reqType = (MPCRequestType)objIn.readObject();

            //reading the id of the object call
            int idSize = objIn.readInt();
            byte[] idBytes = new byte[idSize];
            objIn.read(idBytes);
            String id = new String(idBytes);

            // Only do the operation (marked) if the call is not already executed
            switch (reqType) {
                case SPLIT:
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to write("+newVal+") in cache");
                        objOut.writeObject(splittedSecret);
                        cachedCalls.put(id, splittedSecret);
                        // actual operation
                    }
                    else
                    {
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case READ:
                    int pieceNum = objIn.readInt();
//                    System.out.println("piece number: " + pieceNum);
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to write("+newVal+") in cache");
                        switch (pieceNum){
                            case 1:
                                objOut.writeObject(splittedSecret.getFirst());
                                cachedCalls.put(id, splittedSecret.getFirst());
                                break;
                            case 2:
                                objOut.writeObject(splittedSecret.getSecond());
                                cachedCalls.put(id, splittedSecret.getSecond());
                                break;
                            case 3:
                                objOut.writeObject(splittedSecret.getThird());
                                cachedCalls.put(id, splittedSecret.getThird());
                                break;
                        }
                    }
                    else
                    {
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "in appExecuteOrdered only split and read operations are supported");
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
            logger.log(Level.SEVERE, "Occurred during mpc operation execution", e);
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
            MPCRequestType reqType = (MPCRequestType)objIn.readObject();

            //reading the id of the object call
            int idSize = objIn.readInt();
            byte[] idBytes = new byte[idSize];
            objIn.read(idBytes);
            String id = new String(idBytes);

//            System.out.println(reqType);


            switch (reqType) {
                case SPLIT:
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to write("+newVal+") in cache");
                        objOut.writeObject(splittedSecret);
                        cachedCalls.put(id, splittedSecret);
                        // actual operation
                    }
                    else
                    {
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                case READ:
                    int pieceNum = objIn.readInt();
//                    System.out.println("piece number: " + pieceNum);
                    if(!cachedCalls.containsKey(id)) {
//                        logger.log(Level.WARNING, "putting id " + id + " call to write("+newVal+") in cache");
                        switch (pieceNum){
                            case 1:
                                objOut.writeObject(splittedSecret.getFirst());
                                cachedCalls.put(id, splittedSecret.getFirst());
                                break;
                            case 2:
                                objOut.writeObject(splittedSecret.getSecond());
                                cachedCalls.put(id, splittedSecret.getSecond());
                                break;
                            case 3:
                                objOut.writeObject(splittedSecret.getThird());
                                cachedCalls.put(id, splittedSecret.getThird());
                                break;
                        }
                    }
                    else
                    {
                        objOut.writeObject(cachedCalls.get(id));
                    }
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "in appExecuteunOrdered splid and read operations are supported");
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
            logger.log(Level.SEVERE, "Occurred during mpc operation execution", e);
        }
        return reply;
    }

    @Override
    public byte[] getSnapshot() {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
            objOut.writeObject(memory);
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
            memory = (int) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error while installing snapshot", e);
        }
    }
}
