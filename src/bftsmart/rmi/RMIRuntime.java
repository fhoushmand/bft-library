package bftsmart.rmi;

import bftsmart.reconfiguration.ServerViewController;
import bftsmart.usecase.PartitionedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

// 0,1,2,3,4,5,6 -> A servers??
// 7,8,9,10 -> B servers??
// 11 -> Client server??

// cluster 1 -> i1 == A
// cluster 2 -> i2 == b
// cluster 3 -> a == all
public class RMIRuntime{

    int processNumber;
    ServerCommunicationSystem cs;
    ServerViewController viewController;
    HashMap<String, LinkedList<RTMessage>> messages = new HashMap<>();

    PartitionedObject obj;

    HashMap<String,int[]> methodsHosts;
    HashMap<String,Quorum> methodsQuorums;

    HashMap<String,Class[]> methodArgs;

    public RMIRuntime(int p, PartitionedObject object) throws Exception
    {
        this.processNumber = p;
        this.obj = object;
        viewController = new ServerViewController(processNumber, "myconfig", null, 1);
        cs = new ServerCommunicationSystem(viewController, new MessageHandler());

        methodArgs = new HashMap<>();
        methodArgs.put("m4", new Class[]{Integer.class});
        methodArgs.put("m3", new Class[]{Integer.class});
        methodArgs.put("m2", new Class[]{});
        methodArgs.put("m1", new Class[]{});
        methodArgs.put("ret", new Class[]{Integer.class});




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

        CheckExecution executionChecker = new CheckExecution(messages);
        executionChecker.start();
        cs.start();
    }

    public HashMap<String, int[]> getMethodsHosts() {
        return methodsHosts;
    }

    public HashMap<String, Quorum> getMethodsQuorums() {
        return methodsQuorums;
    }

    public void invoke(String method, int v, int[] q)
    {
        int id = 0;
        RTMessage tmm = new RTMessage(processNumber, id++, 0, method.getBytes(), v, null, RTMessageType.UNORDERED_REQUEST);
        cs.send(q, tmm);
    }

    class MessageHandler extends MessageHandlerRMI {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public MessageHandler() {}

        @SuppressWarnings("unchecked")
        protected void processData(RTMessage sm) {
            String methodName = sm.getMethodName();
            if(!messages.containsKey(methodName)) {
                LinkedList<RTMessage> list = new LinkedList<>();
                list.add(sm);
                messages.put(methodName, list);
            }
            else
                messages.get(methodName).add(sm);

            System.out.println("received invocation for " + methodName + " with argument(s) " + sm.getArg() + " from " + sm.getSender() + " at node " + processNumber);
        }
    }

    class CheckExecution extends Thread
    {
        HashMap<String, LinkedList<RTMessage>> messages;
        public CheckExecution(HashMap<String, LinkedList<RTMessage>> m)
        {
            messages = m;
        }
        @Override
        public void run() {
            while(true)
            {
                for(Map.Entry<String,LinkedList<RTMessage>> mEntry : messages.entrySet())
                {
                    String method = mEntry.getKey();
                    Quorum receivedQ = new Quorum();
                    int arg = 0;
                    for(RTMessage r : mEntry.getValue()) {
                        receivedQ.addNode(r.getSender());
                        arg = (Integer)r.getArg();
                    }
                    //TODO should this check be subset??
                    if(receivedQ.equals(methodsQuorums.get(method))) {
                        try {
                            //TODO maybe later inject a class with partitioned methods - Done??
                            Method m = obj.getClass().getMethod(method, methodArgs.get(method));
                            m.invoke(obj, arg);
                            messages.get(method).clear();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        RMIRuntime runtime = new RMIRuntime(Integer.valueOf(args[0]), null);
        runtime.cs.start();
        Thread.sleep(5000);
        String work = "work";
        Scanner input = new Scanner(System.in);
        int id = 0;
        while(!work.equals("exit")){
            work = input.nextLine();
            RTMessage tmm = new RTMessage(Integer.valueOf(args[0]), id++, 0, null, null, work.getBytes(), RTMessageType.UNORDERED_REQUEST);
            runtime.cs.send(new int[]{0,1}, tmm);
        }
    }
}
