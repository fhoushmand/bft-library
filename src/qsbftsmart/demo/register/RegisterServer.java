package qsbftsmart.demo.register;

import qsbftsmart.demo.map.MapServer;
import qsbftsmart.tom.MessageContext;
import qsbftsmart.tom.ServiceReplica;
import qsbftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterServer<T> extends DefaultSingleRecoverable {

    private T memory;
    private Logger logger;

    public RegisterServer(int id) {
        logger = Logger.getLogger(MapServer.class.getName());
        new ServiceReplica(id, this, this);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: demo.map.RegisterServer <Type> <server id>");
            System.exit(-1);
        }
        String type = args[0];
        switch (type){
            case "String":
                new RegisterServer<String>(Integer.parseInt(args[1]));
                break;
            case "Integer":
                new RegisterServer<Integer>(Integer.parseInt(args[1]));
                break;
            case "Float":
                new RegisterServer<Float>(Integer.parseInt(args[1]));
                break;
            default:
                throw new RuntimeException("Type is not supported");
        }
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        T newVal;
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
             RegisterRequestType reqType = (RegisterRequestType)objIn.readObject();
             switch (reqType) {
                 case WRITE:
                    newVal = (T)objIn.readObject();
                    objOut.writeObject(memory);
                    memory = newVal;
                    hasReply = true;
                    break;
                default:
                    logger.log(Level.WARNING, "in appExecuteOrdered only write operations are supported");
            }
            if (hasReply) {
                objOut.flush();
                byteOut.flush();
                reply = byteOut.toByteArray();
            } else {
                reply = new byte[0];
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Occurred during register operation execution", e);
        }
        return reply;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        byte[] reply = null;
        boolean hasReply = false;

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
            RegisterRequestType reqType = (RegisterRequestType)objIn.readObject();
            switch (reqType) {
                case READ:
                    objOut.writeObject(memory);
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
            logger.log(Level.SEVERE, "Occurred during register operation execution", e);
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
            memory = (T)objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error while installing snapshot", e);
        }
    }
}
