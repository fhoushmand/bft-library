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
import java.util.concurrent.atomic.AtomicInteger;
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

public class RMIRuntime{

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
    HashMap<RTMessage,Quorum> network = new HashMap<>();


    // To synchronize access to the messages (network map)
    ReentrantLock messagesLock = new ReentrantLock();

    //counter for the object method invocations
    //counter is simply a sequence number appended to operationId to uniquely identify method invocations
    //n
    AtomicInteger n = new AtomicInteger(0);
    //methodIdentifier -> methodId::counter
    //operationId in the RTMessage is the counter of the runtime

    //mapping from methodIdentifier -> return value
    //this is to make sure that we don't execute a method multiple times
    HashMap<String,Object> methodsRecord = new HashMap<>();

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

        RMIRuntime runtime = new RMIRuntime(id, clusterId, o);
        o.setRuntime(runtime);

        if (className.equals("bftsmart.usecase.oblivioustransfer.OTClient"))
        {
            for(int i = 0; i < 10; i++)
                ((OTClient)o).transfer(i);
            runtime.shutdown();
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
    public Object invokeObj(String obj, String method, Object... v)
    {
        String methodId = obj+"_"+method+"::"+n.get();
        if(methodsRecord.containsKey(methodId))
            return methodsRecord.get(methodId);
        else{
            try {
                //TODO maybe later inject a class with partitioned methods - Done??
                Method m = objectsState.get(obj).getClass().getMethod(method, methodArgs.get(obj+"_"+method));
                Object returnValue = executeMethod(m, objectsState.get(obj), v);
                methodsRecord.put(methodId, returnValue);
                return returnValue;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //ThisCallSend
    //TODO hard-coded argument???
    public void invoke(String method, int v)
    {
        String mId = method + n.getAndIncrement();
        RTMessage tmm = new RTMessage(id, mId.getBytes(), method.getBytes(), v, null);
        System.out.println("sending message with id: " + tmm.toString() + " to " + String.join(" ", Arrays.stream(methodsHosts.get(method)).mapToObj(String::valueOf).toArray(String[]::new)));
        System.out.println("sending method with id: " + mId);
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
            else
                network.get(sm).addNode(sm.getSender());
            logger.trace("received invocation for {} with argument(s) {} from {} at node {}", methodName, sm.getArg(), sm.getSender(), id);
            checkExecution();

        }
    }

    //ThisCallExec
    // Previously, this was checked by another thread
    public void checkExecution()
    {
        ArrayList<RTMessage> methodsToRemove = new ArrayList<>();
        logger.trace("messages: " + network);
        for(Map.Entry<RTMessage,Quorum> mEntry : network.entrySet())
        {
            String method = mEntry.getKey().getMethodName();
            Quorum receivedQ = mEntry.getValue();

            if(receivedQ.isSubsetEqual(methodsQuorums.get(method))) {
                Class[] argumentsTypeArray = methodArgs.get(method);
                // to deserialize the arguments received from the network
                // we have to make sure to pass the correct number of
                // arguments based on the methodArgs map
                //TODO we also need to cast each argument correctly
                //TODO support multi arguments. for now we only support
                //TODO one argument. need to pass an array of objects as argument
                //TODO to the network and deserialize the object array
                Object[] args = new Object[argumentsTypeArray.length];
                for(int i = 0; i < argumentsTypeArray.length; i++)
                {
                    if(argumentsTypeArray[i].equals(Integer.class))
                        args[i] = (Integer) mEntry.getKey().getArg();
                    else if(argumentsTypeArray[i].equals(Boolean.class))
                    {
                        args[i] = (Boolean) mEntry.getKey().getArg();
                    }
                    else
                        throw new RuntimeException("unsupported type " + methodArgs.get(method));
                }

                try {
                    //TODO maybe later inject a class with partitioned methods - Done??
                    Method m = obj.getClass().getMethod(method, methodArgs.get(method));
                    executeMethod(m, obj, args);
                    methodsToRemove.add(mEntry.getKey());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        for(RTMessage removedMethod : methodsToRemove)
            network.remove(removedMethod);

    }

    public Object executeMethod(Method m, Object obj, Object... args)
    {
        try {
            System.out.println("executing method " + m.getName() + " at " + id);
            return m.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
