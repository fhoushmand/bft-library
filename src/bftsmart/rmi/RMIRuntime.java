package bftsmart.rmi;

import bftsmart.demo.register.BooleanRegisterServer;
import bftsmart.demo.register.IntegerRegisterServer;
import bftsmart.demo.register.RegisterClient;
import bftsmart.reconfiguration.ServerViewController;
import bftsmart.usecase.OTA;
import bftsmart.usecase.OTClient;
import bftsmart.usecase.PartitionedObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// 0,1,2,3,4,5,6 -> A servers??
// 7,8,9,10 -> B servers??
// 11 -> Client server??

// cluster 1 -> i1 == A
// cluster 2 -> i2 == b
// cluster 3 -> a == all


public class RMIRuntime{

    // id of this process (runtime)
    // this id is global to all the clusters participants
    int id;


//    int clusterId;

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
    HashMap<String,Quorum> methodsQuorums;

    // A mapping to store the argument type of the all the methods
    HashMap<String,Class[]> methodArgs;


    public static void main(String[] args) throws Exception{
        int id = Integer.valueOf(args[0]);
        // cluster id responsible for replicating the piece of
        // information in the partitioned object. for OTA it is 1
        // and for OTB it is 3
        //TODO need to make it general for other partitioned objects with multiple object fileds
        int clusterId = Integer.valueOf(args[1]);
        //name of the class to host
        String className = args[2]; //"bftsmart.usecase.OTA"

        PartitionedObject o = (PartitionedObject) Class.forName(className).getConstructor().newInstance();

        RMIRuntime runtime = new RMIRuntime(id, clusterId, o);
        o.setRuntime(runtime);

//        Thread.sleep(5000);

        if (className.equals("bftsmart.usecase.OTClient"))
        {
            String work = "";
            Scanner input = new Scanner(System.in);
//            int uniqueId = 0;
            while(!work.equals("exit")){
                work = input.nextLine();
//                RTMessage tmm = new RTMessage(Integer.valueOf(args[0]), id++, work.getBytes(), null, null);
//                runtime.cs.send(new int[]{0,1}, tmm);
                ((OTClient)o).transfer(Integer.valueOf(input.nextLine()));
            }
            runtime.shutdown();
        }
//
    }

    public RMIRuntime(int p, int clusterId, PartitionedObject object) throws Exception
    {
        this.id = p;
        this.obj = object;

        viewController = new ServerViewController(id, "myconfig", null, 1);
//        cs = new ServerCommunicationSystem(viewController, new MessageHandler());

        methodArgs = new HashMap<>();
        methodArgs.put("m4", new Class[]{Integer.class});
        methodArgs.put("m3", new Class[]{Integer.class});
        methodArgs.put("m2", new Class[]{});
        methodArgs.put("m1", new Class[]{});
        methodArgs.put("ret", new Class[]{Integer.class});

        //object fields methods
        methodArgs.put("i1_read", new Class[]{});
        methodArgs.put("i2_read", new Class[]{});
        methodArgs.put("a_read", new Class[]{});
        methodArgs.put("i1_write", new Class[]{Integer.class});
        methodArgs.put("i2_write", new Class[]{Integer.class});
        methodArgs.put("a_write", new Class[]{Boolean.class});


        for (Field field : obj.getClass().getFields())
        {
            objectsState.put(field.getName(), field.get(obj));
            System.out.println(field.getType());
            if(field.getType().equals(Integer.class)) {
                Integer initValue = (Integer) field.get(obj);
                objectsState.put(field.getName(), new RegisterClient<Integer>(id, clusterId));
                new IntegerRegisterServer(initValue, p, clusterId); //replication of i1 and i2

            }
            else if(field.getType().equals(Boolean.class)) {
                Boolean initValue = (Boolean) field.get(obj);
                new BooleanRegisterServer(initValue, p, 3); //replication of a
                objectsState.put(field.getName(), new RegisterClient<Boolean>(id, 3));
            }
        }

        methodsHosts = new HashMap<>();
        methodsHosts.put("m1", new int[]{0,1,2,3,4});
        methodsHosts.put("m2", new int[]{7,8,9,10});
        methodsHosts.put("m3", new int[]{0,1,2,3,4});
        methodsHosts.put("m4", new int[]{0,1,2,3,4});
        methodsHosts.put("ret", new int[]{11});



        // quorum must be sorted ind ascending order
        methodsQuorums = new HashMap<>();
        Quorum m4q = new Quorum();
        m4q.addNode(11);
        methodsQuorums.put("m4", m4q);
        Quorum m3q = new Quorum();
        m3q.addNode(0);
        m3q.addNode(1);
        m3q.addNode(2);
        methodsQuorums.put("m3", m3q);
        Quorum m2q = new Quorum();
        m2q.addNode(7);
        m2q.addNode(8);
        methodsQuorums.put("m2", m2q);
        Quorum m1q = new Quorum();
        m1q.addNode(0);
        m1q.addNode(1);
        m1q.addNode(2);
        methodsQuorums.put("m1", m1q);
        Quorum retq = new Quorum();
        retq.addNode(0);
        retq.addNode(1);
        retq.addNode(2);
        methodsQuorums.put("ret", retq);

        CheckExecution executionChecker = new CheckExecution();
        executionChecker.start();
        cs.start();
    }

    public void shutdown()
    {
        cs.shutdown();
    }

    public HashMap<String, int[]> getMethodsHosts() {
        return methodsHosts;
    }

//    ObjCall
    public Object invokeObj(String obj, String method, Object... v)
    {
        String methodId = obj+"_"+method+"::"+n.get();
        if(methodsRecord.containsKey(methodId))
            return methodsRecord.get(methodId);
        else{
            try {
                //TODO maybe later inject a class with partitioned methods - Done??
                Method m = objectsState.get(obj).getClass().getMethod(method, methodArgs.get(obj+"_"+method));
                Object returnValue = m.invoke(objectsState.get(obj), v);
                methodsRecord.put(methodId, returnValue);
                return returnValue;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //ThisCallSend
    //TODO hard-coded argument???
    public void invoke(String method, int v)
    {
        RTMessage tmm = new RTMessage(id, n.getAndIncrement(), method.getBytes(), v, null);
        System.out.println("sending message with id: " + tmm.toString());
        System.out.println("sending method with id: " + tmm.getMethodIdentifier());
        cs.send(methodsHosts.get(method), tmm);
    }

    //ThisCallReceive
    class MessageHandler extends MessageHandlerRMI {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public MessageHandler() {}

        @SuppressWarnings("unchecked")
        protected void processData(RTMessage sm) {
            messagesLock.lock();
            String methodName = sm.getMethodName();
            if(!network.containsKey(sm)) {
                Quorum q = new Quorum(sm.getSender());
                network.put(sm, q);
            }
            else
                network.get(sm).addNode(sm.getSender());
            messagesLock.unlock();
            System.out.println("received invocation for " + methodName + " with argument(s) " + sm.getArg() + " from " + sm.getSender() + " at node " + id);
        }
    }

    //ThisCallExec
    class CheckExecution extends Thread
    {
        public CheckExecution()
        {
        }
        @Override
        public void run() {

            while(true)
            {
                ArrayList<RTMessage> methodsToRemove = new ArrayList<>();
                messagesLock.lock();
                for(Map.Entry<RTMessage,Quorum> mEntry : network.entrySet())
                {
                    String method = mEntry.getKey().getMethodName();
                    Quorum receivedQ = mEntry.getValue();

                    //TODO should this check be subset??
                    if(receivedQ.equals(methodsQuorums.get(method))) {
                        Class[] argumentsTypeArray = methodArgs.get(method);
                        // to deserialize the arguments received from the network
                        // we have to make sure to pass the correct number of
                        // arguments based on the methodArgs map
                        //TODO we also need to cast each argument correctly
                        //TODO support multi arguments. for now we only support
                        // one argument. need to pass an array of objects as argument
                        // to the network and deserialize the object array
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
                            Object returnValue = m.invoke(obj, args);
                            methodsToRemove.add(mEntry.getKey());
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
                for(RTMessage removedMethod : methodsToRemove)
                {
                    network.remove(removedMethod);
                }
                messagesLock.unlock();
            }
        }
    }
}
