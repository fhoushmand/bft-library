package bftsmart.runtime;

import bftsmart.demo.register.*;
import bftsmart.reconfiguration.ServerViewController;
import bftsmart.runtime.quorum.*;
import bftsmart.usecase.Client;
import bftsmart.usecase.Spec;
import bftsmart.usecase.auction.AuctionClient;
import bftsmart.usecase.PartitionedObject;
import bftsmart.usecase.friendmap.FriendMapClient;
import bftsmart.usecase.mpc.MPCClient;
import bftsmart.usecase.obltransfer.OblTransferClient;
import bftsmart.usecase.onetimetransfer.OTTClient;
import bftsmart.usecase.ticket.TicketSystemClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11

// cluster 1 -> i1 == A
// cluster 2 -> i2 == b
// cluster 3 -> a == all

// Quorum is a set of hosts
// Quorum system is a set of quorums

// host is simply a process

public class RMIRuntime extends Thread{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // set this to true to read configs from testconfig and testmyconfig folders
    public static boolean test = false;

    public static String CONFIGURATION;// = "(A:1;B:1)";
//    public static String CONFIGURATION = "(A:1;B:1;C:1)";

    public Spec spec;

    // id of this process (runtime)
    // this id is global to all the clusters participants
    int id;

    // to handle server-to-server communications
    ServerCommunicationSystem cs;
    ServerViewController viewController;

    //mapping from <methodIdentifier,methodArgument> -> Quorum
    ConcurrentHashMap<MethodCallMessage,Quorum> network = new ConcurrentHashMap<>();

    //mapping from <methodIdentifier,methodArgument> -> Quorum
    ConcurrentHashMap<ObjCallMessage,Quorum> objCallReceived = new ConcurrentHashMap<>();

    //sequential objects state
    //mapping from objects(names) to states (objects state)
    HashMap<String,Object> objectsState = new HashMap<>();

    // instance of the hosted class.
    // This represents the partitioned object therefore it is subclass of PartitionedObject
    PartitionedObject obj;

    // The id of the processes that host the methods in the given partitioned class
    HashMap<String,H> methodsHosts;

    // The quorum required for the methods to be able to execute
    HashMap<String, Q> methodsQuorums;

    HashMap<String, Q> objectsQuorums;

    // A mapping to store the argument type of all the methods
    HashMap<String,Class[]> methodArgs;

    private HashMap<String, H> objectsH;

    // reading from command line thread
    CMDReader inputReader;

    HashMap<Integer,Long> execs;

    double avgResTime;

    public RMIRuntime(int p, Spec spec, PartitionedObject o) throws Exception
    {
        this.id = p;
        this.obj = o;
        this.spec = spec;
        execs = new HashMap<>();
//
//        if(test)
//            viewController = new ServerViewController(id, "testconfig", null, 1);
//        else
        viewController = new ServerViewController(id, "runtimeconfig_"+CONFIGURATION, null, 1);

        cs = new ServerCommunicationSystem(viewController, new MessageHandler(), spec);

        objectsH = spec.getObjectsH();
        initObjectState();

        methodArgs = spec.getArgsMap();
        methodsHosts = spec.getMethodsH();
        methodsQuorums = spec.getMethodsQ();
        objectsQuorums = spec.getObjectsQ();


        cs.start();

        obj.setRuntime(this);
    }

    //TODO the clusterid is hardcoded for the OT example
    private void initObjectState() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Map.Entry<String,H> placement : objectsH.entrySet())
            objectsState.put(
                    placement.getKey(),
                    spec.getObjectFieldTypeByName(placement.getKey()).getConstructor(Integer.class, Integer.class)
                            .newInstance(id, spec.getClusterIDByObjectField(placement.getKey()))
            );
    }

    @Override
    public void run() {
        if (obj instanceof Client) {
            try {
                Thread.sleep(45000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            inputReader.start();
        }

        while (true)
        {
            try {
                Thread.sleep(10);
                if (obj instanceof Client) {
                    if(obj instanceof OblTransferClient || obj instanceof OTTClient || obj instanceof bftsmart.usecase.onetimetransfer_optimized.OTTClient) {
                        if (obj.responseReceived == CMDReader.TRANSFER_USECASES_REP)
                            break;
                    }
                    else if(obj instanceof TicketSystemClient)
                    {
                        if (obj.responseReceived == CMDReader.TICKET_USECASE_REP)
                            break;
                    }
                    else if(obj instanceof AuctionClient)
                    {
                        if (obj.responseReceived == CMDReader.AUCTION_USECASE_REP)
                            break;
                    }
                    else if(obj instanceof MPCClient)
                    {
                        if (obj.responseReceived == CMDReader.MPC_USECASES_REP)
                            break;
                    }
                    else if(obj instanceof FriendMapClient)
                    {
                        if (obj.responseReceived == CMDReader.FRIENDMAP_USECASES_REP)
                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkExecution();
        }

        double avgResponseTime = 0;
        int count = 0;
        // calculate statistics
        for(int i : execs.keySet()) {
            if(count++ != 0)
                avgResponseTime += execs.get(i);
        }
        avgResTime = avgResponseTime/execs.keySet().size();

//        if(obj instanceof AuctionClient) {
//            for (long l : ((AuctionClient) obj).responseTimes)
//                System.out.print(l + ",");
//            System.out.println("##########");
//            for (Map.Entry<Integer, ArrayList<Long>> entry : ((AuctionClient) obj).requestresponseTimes.entrySet()) {
//                System.out.print(entry.getKey() + ":");
//                long avg = 0;
//                for (long l : entry.getValue())
//                    avg += l;
//                System.out.println(avg / entry.getValue().size());
//            }
//        }

        System.out.println("Average Response Time for " + execs.keySet().size() + " calls = " + avgResTime + "(ms)");

    }

    public void shutdown()
    {
        for (Object o : objectsState.values())
        {
            if(o instanceof IntegerRegisterClient)
                ((IntegerRegisterClient)o).serviceProxy.close();
            if(o instanceof BooleanRegisterClient)
                ((BooleanRegisterClient)o).serviceProxy.close();
        }
        cs.shutdown();
    }


    //ThisCallSend
    public void invoke(String method, String callerId, Integer n, Object... args)
    {
        String mId = callerId + "::" + n;
        MethodCallMessage tmm = new MethodCallMessage(id, mId.getBytes(), method.getBytes(), args);
        tmm.setN(n);
        cs.send(methodsHosts.get(method).toIntArray(), tmm);
    }

    //ThisCallSend
    public ObjCallMessage sendObjectCall(String method, String callingMethod, String callerId, Integer n, Object... args)
    {
        String mId = callerId + "::" + n;
        ObjCallMessage tmm = new ObjCallMessage(id, mId.getBytes(), method.getBytes(), args, callingMethod.getBytes());
        tmm.setN(n);
        cs.send(methodsHosts.get(callingMethod).toIntArray(), tmm);
        return tmm;
    }

    //ThisCallReceive
    class MessageHandler extends MessageHandlerRMI {
        public MessageHandler() {}

        protected void processData(RTMessage sm) {


            if(sm instanceof ObjCallMessage)
            {
                if(!objCallReceived.containsKey(sm)) {
                    Quorum q = new Quorum();
                    q.addNode(sm.getSender());
                    objCallReceived.put((ObjCallMessage) sm, q);
                }
                else {
                    // don't accept messages for a quorum which is bot -> bot union n = bot
                    if(!objCallReceived.get(sm).isBot())
                        objCallReceived.get(sm).addNode(sm.getSender());
                }
            }
            else if(sm instanceof MethodCallMessage)
            {
                if(!network.containsKey(sm)) {
                    Quorum q = new Quorum();
                    q.addNode(sm.getSender());
                    network.put((MethodCallMessage) sm, q);
                }
                else {
                    // don't accept messages for a quorum which is bot -> bot union n = bot
                    if(!network.get(sm).isBot())
                        network.get(sm).addNode(sm.getSender());
                }
            }
//            else if(sm instanceof ShutdownRuntimeMessage) {
//                shutdown();
//            }

        }
    }

    // ObjCall
    // Must block until hear from Q to execute this object call
    // Q is the quorum of the callingMethod
    public Object invokeObj(String obj, String method, String callingMethod, String callerId, Integer n, Object... args)
    {
        ObjCallMessage msgSent = null;
        try {
            int argsLength = args == null ? 0 : args.length;

            String objectCall = obj+"-"+method;
            String mId = callerId + "::" + n;
            // the extra argument is the id of this object call
            Object[] objectCallArgs = new Object[argsLength + 1];
            int i = 0;
            for (; i < argsLength; i++)
                objectCallArgs[i] = args[i];
            objectCallArgs[i] = mId;
            logger.trace("blocked until receive {} call to object {}", objectsQuorums.get(obj), mId);
            // send object call to the quorum
            msgSent = sendObjectCall(objectCall, callingMethod, callerId, n, objectCallArgs);

            // block until get Q messages to execute object call
            do{

            } while (objCallReceived.get(msgSent) == null);
            do{

            } while (!objCallReceived.get(msgSent).isSuperSetEqual(objectsQuorums.get(obj)));

            logger.trace("unblocking object call {}", mId);
            // mark it as bot and clear the memory
            objCallReceived.get(msgSent).setBot();
            logger.trace("obj call {} with method id {}", objectCall, mId);
            Method m = objectsState.get(obj).getClass().getMethod(method, methodArgs.get(objectCall));
            Object returnValue = executeMethod(m, objectsState.get(obj), objectCallArgs);
            return returnValue;
        }
        //TODO check for NPE, see why the blocking while instruction might cause it
        catch (NullPointerException | NoSuchMethodException e)
        {
            e.printStackTrace();
//            System.out.println("received object call reqs: " + objCallReceived);
//            System.out.println("received object call reqs for message " + msgSent + " :" + objCallReceived.get(msgSent));
//            System.out.println("Calling method: " + callingMethod);
//            System.out.println("Hosts of the calling method: " + methodsHosts.get(callingMethod));
        }
//        try {
//            Method m = objectsState.get(obj).getClass().getMethod(method, methodArgs.get(objectCall));
//            Object returnValue = executeMethod(m, objectsState.get(obj), objectCallArgs);
//            return returnValue;
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
        logger.error("must never happen");
        return null;
    }

    // ThisCallExec
    // Previously, this was checked by another thread
    public void checkExecution()
    {
//        logger.trace("messages: " + network);
        for (Map.Entry<MethodCallMessage, Quorum> mEntry : network.entrySet()) {
            String method = mEntry.getKey().getMethodName();
            Quorum receivedQ = mEntry.getValue();

            // don't consider quorums that are bot
            if (receivedQ.isBot())
                continue;

            if (receivedQ.isSuperSetEqual(methodsQuorums.get(method))) {
                Class[] argumentsTypeArray = methodArgs.get(method);
                // to deserialize the arguments received from the network
                // we have to make sure to pass the correct number of
                // arguments based on the methodArgs map
                Object[] args = new Object[argumentsTypeArray.length];
                // setting the callerId and n except if it is "ret"
//                if (!mEntry.getKey().getMethodName().equals("ret")) {
                    args[0] = mEntry.getKey().getOperationId();
                    args[1] = mEntry.getKey().getN();
//                }
                if (mEntry.getKey().getArg() != null) {
                    for (int j = 0; j < ((Object[]) mEntry.getKey().getArg()).length; j++) {
//                        int i = mEntry.getKey().getMethodName().equals("ret") ? 0 : j + 2;
                        int i = j+2;
                        args[i] = ((Object[]) mEntry.getKey().getArg())[j];
                    }
                }

                try {
                    Method m = obj.getClass().getMethod(method, methodArgs.get(method));
                    executeMethod(m, obj, args);
                    // to make this quorum bot
                    network.get(mEntry.getKey()).setBot();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object executeMethod(Method m, Object obj, Object... args)
    {
        try {
            logger.trace("executing method {}", m.getName());
            Object returnValue = m.invoke(obj, args);

            if(!m.getReturnType().equals(Void.TYPE)) {
                logger.trace("return value: {}", returnValue);
            }
            return returnValue;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        logger.error("must never happen!");
        return null;
    }


    public CMDReader getInputReader() {
        return inputReader;
    }

    public void setInputReader(CMDReader inputReader) {
        this.inputReader = inputReader;
    }

    public PartitionedObject getObj() {
        return obj;
    }

    public void setObj(PartitionedObject obj) {
        this.obj = obj;
    }

    public ConcurrentHashMap<MethodCallMessage, Quorum> getNetwork() {
        return network;
    }

    public HashMap<Integer, Long> getExecs() {
        return execs;
    }
}
