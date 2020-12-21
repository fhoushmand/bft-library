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
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

// set of A hosts: 0,1,2,3,4,5,6
// set of B hosts: 7,8,9,10
// client host: 11

// cluster 1 -> i1 == A
// cluster 2 -> i2 == b
// cluster 3 -> a == all

// Quorum is a set of hosts
// Quorum system is a set of quorums

// host is simply a process

public class RMIRuntime{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private java.util.logging.Logger javaLogger = java.util.logging.Logger.getLogger(this.getClass().getName());

    // set this to true to read configs from testconfig and testmyconfig folders
    public static boolean test = false;

    // id of this process (runtime)
    // this id is global to all the clusters participants
    int id;

    // to handle server-to-server communications
    ServerCommunicationSystem cs;
    ServerViewController viewController;

    //mapping from <methodIdentifier,methodArgument> -> Quorum
    HashMap<RTMessage,Quorum> network = new HashMap<>();


    // To synchronize access to the messages (network map)
    ReentrantLock messagesLock = new ReentrantLock();

    //counter for the object method invocations
    //counter is simply a sequence number appended to operationId to uniquely identify method invocations
    //n
//    AtomicInteger n = new AtomicInteger(0);
    //methodIdentifier -> methodId::counter
    //operationId in the RTMessage is the counter of the runtime

    //mapping from methodIdentifier -> return value
    //this is to make sure that we don't execute a method multiple times
//    HashMap<String,Object> methodsRecord = new HashMap<>();

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

    // A mapping to store the argument type of the all the methods
    HashMap<String,Class[]> methodArgs;


    /**
    * @param args [0] is the id of the runtime (unique)
    * @param args [1] cluster id for the replication of the object
        cluster id responsible for replicating the piece of
        information in the partitioned object. for OTA it is 1
        and for OTB it is 3
    * @param args [2] class name of the PartitionedObject subclass
     */
    public static void main(String[] args) throws Exception{
        int id = Integer.valueOf(args[0]);
        // cluster id responsible for replicating the piece of
        // information in the partitioned object. for OTA it is 1
        // and for OTB it is 3
        //TODO need to make it general for other partitioned objects with multiple object fields
        int clusterId = Integer.valueOf(args[1]);
        //name of the class to host
        String className = args[2]; //"bftsmart.usecase.oblivioustransfer.OTA"

        PartitionedObject o = (PartitionedObject) Class.forName(className).getConstructor().newInstance();

        if (className.equals("bftsmart.usecase.oblivioustransfer.OTClient"))
            Thread.sleep(5000);

        RMIRuntime runtime = new RMIRuntime(id, clusterId, o);
        o.setRuntime(runtime);

        Scanner input = new Scanner(System.in);

        if (className.equals("bftsmart.usecase.oblivioustransfer.OTClient"))
        {
//            for(int i = 0; i < 10; i++)
//                ((OTClient)o).transfer(i);
//            ((OTClient)o).transfer(0);
//            Thread.sleep(3000);
//            ((OTClient)o).transfer(1);
//            Thread.sleep(3000);
//            ((OTClient)o).transfer(2);
            while (input.hasNext()) {
                String value = input.nextLine();
                if (value.equals("exit"))
                    break;
                ((OTClient) o).transfer(Integer.valueOf(value));
            }

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

    public void shutdown()
    {
        cs.shutdown();
    }

    // ObjCall
    public Object invokeObj(String obj, String method, Object... args)
    {
        int argsLength = args.length;
        String objectCall = obj+"_"+method;
        // normally it is the line below:
//        String methodId = args[args.length-1] + "::" + n.getAndIncrement();

        int n = (Integer) args[argsLength-1];
        String mId = (String)args[argsLength-2] + "::" + n;

//        String methodId = args[args.length-1] + "::" + method + "::" + n.getAndIncrement();

//        logger.trace("cache: " + methodsRecord);
        javaLogger.log(Level.WARNING, "obj call " + objectCall + " with method id " + mId);
//        if(methodsRecord.containsKey(mId)) {
//            System.out.println("Hitting cache for " + mId + " when calling " + obj + "." + method);
//            return methodsRecord.get(mId);
//        }
//        else{
            try {
                //TODO maybe later inject a class with partitioned methods - Done??
                Method m = objectsState.get(obj).getClass().getMethod(method, methodArgs.get(objectCall));

                // the extra argument is the id of this object call
                Object[] objectCallArgs = new Object[argsLength - 2 + 1];
                int i = 0;
                for (; i < argsLength - 2; i++)
                    objectCallArgs[i] = args[i];
                objectCallArgs[i] = mId;

                Object returnValue = executeMethod(m, objectsState.get(obj), objectCallArgs);
//                methodsRecord.put(mId, returnValue);
                return returnValue;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
//        }
        javaLogger.log(Level.SEVERE, "must never happen");
        return null;
    }

    //ThisCallSend
    //TODO hard-coded argument???
    public void invoke(String method, Object... args)
    {
        int argsLength = args.length;
        // normally it is the line below
//        String mId = (String)args[argsLength-1] + "::" + n.getAndIncrement();

        int n = (Integer) args[argsLength-1];
        String mId = (String)args[argsLength-2] + "::" + n;


//        String mId = args[argsLength-1] + "::" + method + "::" + n.getAndIncrement();
        RTMessage tmm = null;
        if(argsLength > 2)
             tmm = new RTMessage(id, mId.getBytes(), method.getBytes(), (Integer)args[0], null);
        else
            tmm = new RTMessage(id, mId.getBytes(), method.getBytes(), null, null);

        tmm.setN(n);

        cs.send(methodsHosts.get(method), tmm);
    }

    //ThisCallReceive
    class MessageHandler extends MessageHandlerRMI {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public MessageHandler() {}

        protected void processData(RTMessage sm) {
            String methodName = sm.getMethodName();
            if(!network.containsKey(sm)) {
                Quorum q = new Quorum();
                q.addNode(sm.getSender());
                network.put(sm, q);
            }
            else {
                // don't accept messages for a quorum which is bot -> bot union n = bot
                if(!network.get(sm).isBot())
                    network.get(sm).addNode(sm.getSender());
            }
            logger.trace("received invocation for {} with argument(s) {} from {} at node {}", methodName, sm.getArg(), sm.getSender(), id);
            checkExecution();

        }
    }

    //ThisCallExec
    // Previously, this was checked by another thread
    public void checkExecution()
    {
        logger.trace("messages: " + network);
        for(Map.Entry<RTMessage,Quorum> mEntry : network.entrySet())
        {
            String method = mEntry.getKey().getMethodName();
            Quorum receivedQ = mEntry.getValue();

            // don't consider quorums that are bot
            if(receivedQ.isBot())
                continue;

            if(receivedQ.isSubsetEqual(methodsQuorums.get(method))) {
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
                // for ret method all of the arguments are relevant
                for(int i = 0; i< (mEntry.getKey().getMethodName().equals("ret") ? argumentsTypeArray.length : argumentsTypeArray.length - 2); i++)
                {
                    if(argumentsTypeArray[i].equals(Integer.class))
                        args[i] =  mEntry.getKey().getArg();
                    else if(argumentsTypeArray[i].equals(Boolean.class))
                    {
                        args[i] =  mEntry.getKey().getArg();
                    }
                    else
                        throw new RuntimeException("unsupported type " + methodArgs.get(method));
                }
                // setting the callerId except if it is "ret"
                if(!mEntry.getKey().getMethodName().equals("ret")) {
                    args[argumentsTypeArray.length - 1] = mEntry.getKey().getN();
                    args[argumentsTypeArray.length - 2] = mEntry.getKey().getOperationId();
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
//            System.out.println("executing method " + m.getName() + " at " + id);
            logger.trace("executing method {}", m.getName());
            Object returnValue = m.invoke(obj, args);

            // only print return value if it is not void
            if(!m.getReturnType().equals(Void.TYPE)) {
                if (returnValue instanceof Boolean)
                    logger.trace("return value: {}", (Boolean) returnValue);
                else if (returnValue instanceof Integer)
                    logger.trace("return value: {}", (Integer) returnValue);
                else
                    logger.trace("return value: {}", returnValue);
            }
            return returnValue;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        javaLogger.log(Level.SEVERE, "must never happen!");
        return null;
    }
}
