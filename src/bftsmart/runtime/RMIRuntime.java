package bftsmart.runtime;

import bftsmart.demo.register.*;
import bftsmart.reconfiguration.ServerViewController;
import bftsmart.runtime.quorum.*;
import bftsmart.usecase.oblivioustransfer.OTClient;
import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // set this to true to read configs from testconfig and testmyconfig folders
    public static boolean test = false;

    // id of this process (runtime)
    // this id is global to all the clusters participants
    int id;

    // to handle server-to-server communications
    ServerCommunicationSystem cs;
    ServerViewController viewController;

    //mapping from <methodIdentifier,methodArgument> -> Quorum
    ConcurrentHashMap<MethodCallMessage,Quorum> network = new ConcurrentHashMap<MethodCallMessage,Quorum>();

    //mapping from <methodIdentifier,methodArgument> -> Quorum
    HashMap<ObjCallMessage,Quorum> objCallReceived = new HashMap<>();

    //sequential objects state
    //mapping from objects(names) to states (objects state)
    HashMap<String,Object> objectsState = new HashMap<>();

    // instance of the hosted class.
    // This represents the partitioned object therefore it is subclass of PartitionedObject
    PartitionedObject obj;

    // The id of the processes that host the methods in the given partitioned class
    HashMap<String,int[]> methodsHosts;

    // The quorum required for the methods to be able to execute
    HashMap<String,Q> methodsQuorums;

    // A mapping to store the argument type of all the methods
    HashMap<String,Class[]> methodArgs;

    // reading from command line thread
    CMDReader inputReader;

    private ReentrantLock objCallLock = new ReentrantLock();
    private Condition objCallBlock = objCallLock.newCondition();

    public void unblockObjectCall()
    {
        objCallLock.lock();
        objCallBlock.signalAll();
        objCallLock.unlock();
    }

    /**
     * @param args [0] is the id of the runtime (unique)
     * @param args [1] cluster id for the replication of the object
        cluster id responsible for replicating the piece of
        information in the partitioned object. for OTA it is 1
        and for OTB it is 3
     * @param args [2] class name of the PartitionedObject subclass
     */

    public static String className = null;
    public static PartitionedObject o = null;
    public static void main(String[] args) throws Exception{
        int id = Integer.valueOf(args[0]);
        // cluster id responsible for replicating the piece of
        // information in the partitioned object. for OTA it is 1
        // and for OTB it is 3
        //TODO need to make it general for other partitioned objects with multiple object fields
        int clusterId = Integer.valueOf(args[1]);
        //name of the class to host
        className = args[2];

        o = (PartitionedObject) Class.forName(className).getConstructor().newInstance();

        RMIRuntime runtime = new RMIRuntime(id, clusterId, o);
        o.setRuntime(runtime);
        runtime.start();

        //read from standard input
        if (className.equals("bftsmart.usecase.oblivioustransfer.OTClient"))
        {
            LinkedBlockingQueue<String> inputs = new LinkedBlockingQueue<>(100);
            runtime.inputReader = new CMDReader(inputs);
            runtime.inputReader.start();
        }
    }

    public RMIRuntime(int p, int clusterId, PartitionedObject object) throws Exception
    {
        this.id = p;
        this.obj = object;

        if(test)
            viewController = new ServerViewController(id, "testmyconfig", null, 1);
        else
            viewController = new ServerViewController(id, "myconfig", null, 1);
        cs = new ServerCommunicationSystem(viewController, new MessageHandler());


        for (Field field : obj.getClass().getFields())
        {
            objectsState.put(field.getName(), field.get(obj));
            System.out.println(field.getType());
            if(field.getType().equals(Integer.class)) {
                Integer initValue = (Integer) field.get(obj);
                new IntegerRegisterServer(initValue, p, clusterId); //replication of i1 and i2
                objectsState.put(field.getName(), new IntegerRegisterClient(id, clusterId));

            }
            else if(field.getType().equals(Boolean.class)) {
                Boolean initValue = (Boolean) field.get(obj);
                new BooleanRegisterServer(initValue, p, 3); //replication of a
                objectsState.put(field.getName(), new BooleanRegisterClient(id, 3));
            }
        }

        methodArgs = obj.getArgsMap();
        methodsHosts = obj.getMethodsH();
        methodsQuorums = obj.getMethodsQ();

        cs.start();
    }

    @Override
    public void run() {
        if (className.equals("bftsmart.usecase.oblivioustransfer.OTClient")) {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (true)
        {
            try {
                Thread.sleep(100);
                if (className.equals("bftsmart.usecase.oblivioustransfer.OTClient")) {
                    String in = inputReader.getInQueue().poll(100, TimeUnit.MILLISECONDS);
                    if (in != null) {
                        if (in.equals("exit"))
                            break;
                        ((OTClient) o).transfer(Integer.valueOf(in));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkExecution();
        }
    }

    public void shutdown()
    {
        cs.shutdown();
    }


    //ThisCallSend
    //TODO hard-coded argument???
    public void invoke(String method, String callerId, Integer n, Object... args)
    {
        int argsLength = args.length;

        String mId = callerId + "::" + n;

        MethodCallMessage tmm = null;
        if(argsLength >= 1)
            tmm = new MethodCallMessage(id, mId.getBytes(), method.getBytes(), args);
        else
            tmm = new MethodCallMessage(id, mId.getBytes(), method.getBytes(), null);

        tmm.setN(n);
        cs.send(methodsHosts.get(method), tmm);
    }

    //ThisCallSend
    //TODO hard-coded argument???
    public void sendObjectCall(String method, String callingMethod, String callerId, Integer n, Object... args)
    {
        int argsLength = args.length;

        String mId = callerId + "::" + n;

        ObjCallMessage tmm = null;
        if(argsLength >= 1)
            tmm = new ObjCallMessage(id, mId.getBytes(), method.getBytes(), args[0], callingMethod.getBytes());
        else
            tmm = new ObjCallMessage(id, mId.getBytes(), method.getBytes(), null, callingMethod.getBytes());

        tmm.setN(n);
        cs.send(methodsHosts.get(callingMethod), tmm);
    }

    //ThisCallReceive
    class MessageHandler extends MessageHandlerRMI {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

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
                // unblock objcall if we have receive enough messages to call the object method
                if(!objCallReceived.get(sm).isBot() && objCallReceived.get(sm).isSuperSetEqual(methodsHosts.get(((ObjCallMessage) sm).getCallerName())))
                {

                    logger.trace("unblocking object call {}", sm.toString());
                    unblockObjectCall();
                    objCallReceived.get(sm).setBot();
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

        }
    }

    // ObjCall
    // Must block until hear from Q to execute this object call
    // Q is the quorum of the callingMethod
    public Object invokeObj(String obj, String method, String callingMethod, String callerId, Integer n, Object... args)
    {
        int argsLength = args == null ? 0 : args.length;

        String objectCall = obj+"-"+method;
        String mId = callerId + "::" + n;

        // the extra argument is the id of this object call
        Object[] objectCallArgs = new Object[argsLength + 1];
        int i = 0;
        for (; i < argsLength; i++)
            objectCallArgs[i] = args[i];
        objectCallArgs[i] = mId;

        logger.trace("blocked until receive {} call to object {}", methodsHosts.get(callingMethod), mId);
        // send object call to the quorum
        sendObjectCall(objectCall, callingMethod, callerId, n, objectCallArgs);
        // block until get Q messages to execute object call
        logger.trace("unblocking object call {}", mId);
        objCallLock.lock();
        try {
            objCallBlock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            objCallLock.unlock();
        }


        logger.trace("obj call {} with method id {}", objectCall, mId);
        try {
            Method m = objectsState.get(obj).getClass().getMethod(method, methodArgs.get(objectCall));
            Object returnValue = executeMethod(m, objectsState.get(obj), objectCallArgs);
            return returnValue;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        logger.error("must never happen");
        return null;
    }

    // ThisCallExec
    // Previously, this was checked by another thread
    public synchronized void checkExecution()
    {
//        logger.trace("messages: " + network);
        for (Iterator<Map.Entry<MethodCallMessage, Quorum>> it = network.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<MethodCallMessage,Quorum> mEntry = it.next();
            String method = mEntry.getKey().getMethodName();
            Quorum receivedQ = mEntry.getValue();

            // don't consider quorums that are bot
            if(receivedQ.isBot())
                continue;

            if(receivedQ.isSuperSetEqual(methodsQuorums.get(method))) {
                Class[] argumentsTypeArray = methodArgs.get(method);
                // to deserialize the arguments received from the network
                // we have to make sure to pass the correct number of
                // arguments based on the methodArgs map
                //TODO we also need to cast each argument correctly
                //TODO support multi arguments. for now we only support
                //TODO one argument. need to pass an array of objects as argument
                //TODO to the network and deserialize the object array
                // the last argument is the caller method Id
                Object[] args = new Object[argumentsTypeArray.length];

                // setting the callerId and n except if it is "ret"
                if(!mEntry.getKey().getMethodName().equals("ret")) {
                    args[0] = mEntry.getKey().getOperationId();
                    args[1] = mEntry.getKey().getN();
                }
                // for ret method all of the arguments are relevant
                for(int j = 0; j < ((Object[])mEntry.getKey().getArg()).length; j++)
                {
                    int i = j+2;
                    args[i] = ((Object[])mEntry.getKey().getArg())[j];
//                    if(argumentsTypeArray[i].equals(Integer.class))
//                        args[i] =  (Integer)mEntry.getKey().getArg();
//                    else if(argumentsTypeArray[i].equals(Boolean.class))
//                    {
//                        args[i] =  (Boolean)mEntry.getKey().getArg();
//                    }
//                    else
//                        throw new RuntimeException("unsupported type " + methodArgs.get(method));
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
            // only print return value if it is not void
            if(!m.getReturnType().equals(Void.TYPE)) {
                logger.trace("return value: {}", returnValue);
            }
            return returnValue;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.out.println(args.length);
            if(args.length >=2 )
            {
                System.out.println(args[0] + ", " + args[1]);
            }
            else if(args.length == 1)
            {
                System.out.println((String)args[0]);
            }

        }
        logger.error("must never happen!");
        return null;
    }

    //TODO integrate into the code - do we need casting??
    public Object[] castArguments(Class[] argumentsType, Object... args)
    {
        Object[] argumentsArray = new Object[args.length];
        for(int i = 0; i < args.length; i++) {
            if (argumentsType[i].equals(Integer.class))
                argumentsArray[i] = args[i];
            else if (argumentsType[i].equals(Boolean.class)) {
                argumentsArray[i] = args[i];
            } else
                throw new RuntimeException("unsupported type " + argumentsType[i]);
        }
        return argumentsArray;
    }
}
