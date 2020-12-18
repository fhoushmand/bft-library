package bftsmart.usecase;
import bftsmart.runtime.RMIRuntime;
import bftsmart.runtime.quorum.H;
import bftsmart.runtime.quorum.Q;
import bftsmart.runtime.quorum.QAnd;
import bftsmart.runtime.quorum.QOr;
import java.util.HashMap;

public class PartitionedObject {
    protected RMIRuntime runtime;

    // The id of the processes that host the methods in the given partitioned class
    private HashMap<String,int[]> methodsH;

    // The quorum required for the methods to be able to execute
    private HashMap<String,Q> methodsQ;

    // A mapping to store the argument type of the all the methods
    private HashMap<String,Class[]> argsMap;

//    actual oblivious transfer usecase
    public PartitionedObject() {
        //initialize the list host sets
        //TODO pass this information as argument
        H A = new H();
        A.addHost(0);
        A.addHost(1);
        A.addHost(2);
        A.addHost(3);
        A.addHost(4);
        A.addHost(5);
        A.addHost(6);
        H B = new H();
        B.addHost(7);
        B.addHost(8);
        B.addHost(9);
        B.addHost(10);
        H Client = new H();
        Client.addHost(11);

        argsMap = new HashMap<>();
        argsMap.put("m4", new Class[]{Integer.class});
        argsMap.put("m3", new Class[]{Integer.class});
        argsMap.put("m2", new Class[]{});
        argsMap.put("m1", new Class[]{});
        argsMap.put("ret", new Class[]{Integer.class});

        //object fields methods
        argsMap.put("i1_read", new Class[]{});
        argsMap.put("i2_read", new Class[]{});
        argsMap.put("a_read", new Class[]{});
        argsMap.put("i1_write", new Class[]{Integer.class});
        argsMap.put("i2_write", new Class[]{Integer.class});
        argsMap.put("a_write", new Class[]{Boolean.class});


        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(5).toIntArray());
        methodsH.put("m2", B.pickFirst(4).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(5), B.pickFirst(3)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(5), B.pickFirst(3)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());


        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 3), new Q(B, 2)));
        methodsQ.put("m2", new Q(B, 2));
        methodsQ.put("m1", new Q(A, 3));
        methodsQ.put("ret", new QOr(new Q(A, 3), new Q(B, 2)));
    }


    // small test
//    public PartitionedObject() {
//        //initialize the list host sets
//        //TODO pass this information as argument
//        H A = new H();
//        A.addHost(0);
//        A.addHost(1);
//        H B = new H();
//        B.addHost(3);
//        H Client = new H();
//        Client.addHost(4);
//
//        argsMap = new HashMap<>();
//        argsMap.put("m4", new Class[]{Integer.class});
//        argsMap.put("m3", new Class[]{Integer.class});
//        argsMap.put("m2", new Class[]{});
//        argsMap.put("m1", new Class[]{});
//        argsMap.put("ret", new Class[]{Integer.class});
//
//        //object fields methods
//        argsMap.put("i1_read", new Class[]{});
//        argsMap.put("i2_read", new Class[]{});
//        argsMap.put("a_read", new Class[]{});
//        argsMap.put("i1_write", new Class[]{Integer.class});
//        argsMap.put("i2_write", new Class[]{Integer.class});
//        argsMap.put("a_write", new Class[]{Boolean.class});
//
//
//        methodsH = new HashMap<>();
//        methodsH.put("m1", A.pickFirst(2).toIntArray());
//        methodsH.put("m2", B.pickFirst(1).toIntArray());
//        methodsH.put("m3", H.union(A.pickFirst(2), B.pickFirst(1)).toIntArray());
//        methodsH.put("m4", H.union(A.pickFirst(2), B.pickFirst(1)).toIntArray());
//        methodsH.put("ret", Client.pickFirst(1).toIntArray());
//
//
//        // initialize methods qs. there are three possibilities:
//        // 1) Single Q
//        // 2) And of two Qs
//        // 3) Or of two Qs
//        methodsQ = new HashMap<>();
//        methodsQ.put("m4", new Q(Client, 1));
//        methodsQ.put("m3", new QAnd(new Q(A, 1), new Q(B, 1)));
//        methodsQ.put("m2", new Q(B, 1));
//        methodsQ.put("m1", new Q(A, 1));
//        methodsQ.put("ret", new QOr(new Q(A, 1), new Q(B, 1)));
//    }

    public void setRuntime(RMIRuntime runtime) {
        this.runtime = runtime;
    }

    public HashMap<String, int[]> getMethodsH() {
        return methodsH;
    }

    public HashMap<String, Q> getMethodsQ() {
        return methodsQ;
    }

    public HashMap<String, Class[]> getArgsMap() {
        return argsMap;
    }
}
