package bftsmart.usecase;
import bftsmart.runtime.RMIRuntime;
import bftsmart.runtime.quorum.*;
import bftsmart.runtime.quorum.P;
import bftsmart.runtime.quorum.PAnd;
import bftsmart.usecase.auction.OfferInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PartitionedObject {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public int responseReceived = 0;

    protected RMIRuntime runtime;

    private HashMap<Integer,String> hostipMap;

    protected int sequenceNumber = 0;

    public ReentrantLock objCallLock = new ReentrantLock();
    public Condition requestBlock = objCallLock.newCondition();

    // The id of the processes that host the methods in the given partitioned class
    private HashMap<String,int[]> methodsH;

    // The quorum required for the methods to be able to execute
    private HashMap<String, Q> methodsQ;

    private HashMap<String, Q> objectsQ;

    // A mapping to store the argument type of the all the methods
    private HashMap<String,Class[]> argsMap;

    private H allHosts;

    private ArrayList<H> hosts = new ArrayList<>();


//  actual oblivious transfer usecase
    public PartitionedObject(HashMap<Integer,String> hostipMap, String configuration) {
        this.hostipMap = hostipMap;
        try {
            if(configuration.equals("ot-A1-B1"))
                initOT_A1B1("ot-A1-B1");
            else if(configuration.equals("ot-A2-B1"))
                initOT_A2B1("ot-A2-B1");
            else if(configuration.equals("ot-A3-B1"))
                initOT_A3B1("ot-A3-B1");
            else if(configuration.equals("ot-A4-B1"))
                initOT_A4B1("ot-A4-B1");
            else if(configuration.equals("ot-A5-B1"))
                initOT_A5B1("ot-A5-B1");
            else if(configuration.equals("ot-A6-B1"))
                initOT_A6B1("ot-A6-B1");
            else if(configuration.equals("ot-A7-B1"))
                initOT_A7B1("ot-A7-B1");

            else if(configuration.equals("ot-A2-B2"))
                initOT_A2B2("ot-A-2-B2");
            else if(configuration.equals("ot-A3-B2"))
                initOT_A3B2("ot-A3-B2");
            else if(configuration.equals("ot-A3-B3"))
                initOT_A3B3("ot-A3-B3");
            else if(configuration.equals("ot-A4-B4"))
                initOT_A4B4("ot-A4-B4");

            else if(configuration.equals("max3(A:1;B:1;C:1)"))
                initMax_A1B1C1("max3(A:1;B:1;C:1)");

            else if(configuration.equals("ac-A2-B1-C0"))
                initAuction_A2B1("ac-A2-B1-C0");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public PartitionedObject() {
        new PartitionedObject(new HashMap<>(), "");
    }

    public void initializeOT()
    {
        argsMap = new HashMap<>();
        argsMap.put("m4", new Class[]{String.class,Integer.class,Integer.class});
        argsMap.put("m3", new Class[]{String.class,Integer.class,Integer.class});
        argsMap.put("m2", new Class[]{String.class,Integer.class});
        argsMap.put("m1", new Class[]{String.class,Integer.class});

        // always here
        argsMap.put("request", new Class[]{Integer.class});
        argsMap.put("ret", new Class[]{String.class,Integer.class,Integer.class});

        //object fields methods
        argsMap.put("i1-read", new Class[]{String.class});
        argsMap.put("i2-read", new Class[]{String.class});
        argsMap.put("a-read", new Class[]{String.class});
        argsMap.put("i1-write", new Class[]{Integer.class,String.class});
        argsMap.put("i2-write", new Class[]{Integer.class,String.class});
        argsMap.put("a-write", new Class[]{Boolean.class,String.class});
    }

    public void finilaizeOT(String configuration, H A, H B)
    {
        // create and write to hosts.config files
        String configPath = "config_" + configuration;
        File directory = new File(configPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // replication of i1
        writeHostsConfigFile(A, 1, configPath, 11000, 0);
        writeSystemConfigFile(A, 1, configPath);
        // replication of i2
        writeHostsConfigFile(B, 2, configPath, 12000, A.size());
        writeSystemConfigFile(B, 2, configPath);
        // replication of a
        writeHostsConfigFile(H.union(A, B), 3, configPath, 13000, 0);
        writeSystemConfigFile(H.union(A,B), 3, configPath);


        String runtimeConfigPath = "runtimeconfig_" + configuration;
        directory = new File(runtimeConfigPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // create runtime configuration
        writeHostsConfigFile(allHosts, 1, runtimeConfigPath, 14000, 0);
        writeSystemConfigFile(allHosts, 1, runtimeConfigPath);
    }

    public void initializeMax()
    {
        argsMap = new HashMap<>();
        argsMap.put("m4", new Class[]{String.class,Integer.class});
        argsMap.put("m3", new Class[]{String.class,Integer.class,Integer.class});
        argsMap.put("m2", new Class[]{String.class,Integer.class,Integer.class});

        // always here
        argsMap.put("ret", new Class[]{Integer.class});

        //object fields methods
        argsMap.put("a-read", new Class[]{String.class});
        argsMap.put("b-read", new Class[]{String.class});
        argsMap.put("c-read", new Class[]{String.class});
    }

    public void finilaizeMax(String configuration, H A, H B, H C)
    {
        // create and write to hosts.config files
        String configPath = "config_" + configuration;
        File directory = new File(configPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // replication of a
        writeHostsConfigFile(A, 1, configPath, 11000, 0);
        writeSystemConfigFile(A, 1, configPath);
        // replication of b
        writeHostsConfigFile(B, 2, configPath, 12000, A.size());
        writeSystemConfigFile(B, 2, configPath);
        // replication of c
        writeHostsConfigFile(C, 3, configPath, 13000, A.size() + B.size());
        writeSystemConfigFile(C, 3, configPath);


        String runtimeConfigPath = "runtimeconfig_" + configuration;
        directory = new File(runtimeConfigPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // create runtime configuration
        writeHostsConfigFile(allHosts, 1, runtimeConfigPath, 13000, 0);
        writeSystemConfigFile(allHosts, 1, runtimeConfigPath);
    }

    public void initializeAuction()
    {
        argsMap = new HashMap<>();
        argsMap.put("m6", new Class[]{String.class,Integer.class,OfferInfo.class,Integer.class});
        argsMap.put("m5", new Class[]{String.class,Integer.class,Integer.class,Integer.class});
        argsMap.put("m4", new Class[]{String.class,Integer.class,Integer.class});
        argsMap.put("m3", new Class[]{String.class,Integer.class,Integer.class,Integer.class, OfferInfo.class});
        argsMap.put("m2", new Class[]{String.class,Integer.class,Integer.class,Integer.class});
        argsMap.put("m1", new Class[]{String.class,Integer.class,Integer.class});

        // always here
        argsMap.put("request", new Class[]{Integer.class});
        argsMap.put("ret", new Class[]{String.class,Integer.class,OfferInfo.class});

        //object fields methods
        argsMap.put("userAgent-read", new Class[]{String.class});
        argsMap.put("userAgent-updateOffer", new Class[]{OfferInfo.class,String.class});
        argsMap.put("userAgent-declareWinner", new Class[]{Integer.class,String.class});
        argsMap.put("agentA-makeOfferA", new Class[]{Integer.class,Integer.class,String.class});
        argsMap.put("agentB-makeOfferB", new Class[]{Integer.class,Integer.class,String.class});
    }

    public void finilaizeAuction(String configuration, H A, H B, H C)
    {
        // create and write to hosts.config files
        String configPath = "config_" + configuration;
        File directory = new File(configPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // replication of agentA
        writeHostsConfigFile(A, 1, configPath, 11000, 0);
        writeSystemConfigFile(A, 1, configPath);
        // replication of agentB
        writeHostsConfigFile(B, 2, configPath, 12000, A.size());
        writeSystemConfigFile(B, 2, configPath);
        // replication of userAgent
        writeHostsConfigFile(C, 3, configPath, 13000, A.size()+B.size());
        writeSystemConfigFile(C, 3, configPath);


        String runtimeConfigPath = "runtimeconfig_" + configuration;
        directory = new File(runtimeConfigPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // create runtime configuration
        writeHostsConfigFile(allHosts, 1, runtimeConfigPath, 14000, 0);
        writeSystemConfigFile(allHosts, 1, runtimeConfigPath);
    }

    public void initMax_A1B1C1(String configuration) {
        initializeMax();
        //initialize the list host sets
        //TODO pass this information as argument
        H A = new H();
        A.addHost(0);
        A.addHost(1);
        A.addHost(2);
        A.addHost(3);
        hosts.add(A);
        H B = new H();
        B.addHost(4);
        B.addHost(5);
        B.addHost(6);
        B.addHost(7);
        hosts.add(B);
        H C = new H();
        C.addHost(8);
        C.addHost(9);
        C.addHost(10);
        C.addHost(11);
        hosts.add(C);
        H Client = new H();
        Client.addHost(12);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, C);
        allHosts = H.union(allHosts, Client);


        methodsH = new HashMap<>();
        methodsH.put("m4", A.pickFirst(3).toIntArray());
        methodsH.put("m3", B.pickFirst(3).toIntArray());
        methodsH.put("m2", C.pickFirst(3).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new P(A, 2));
        methodsQ.put("m2", new P(B, 2));
        methodsQ.put("ret", new P(C, 2));

        finilaizeMax(configuration, A, B, C);
    }

    // The number after the host character represents the availability and integrity type
    // (A1B1 means this configuration can withstand upto 1 byzantine nodes in A and 1 node in B
    public void initOT_A1B1(String configuration) {
        initializeOT();
        //initialize the list host sets
        //TODO pass this information as argument
        H A = new H();
        A.addHost(0);
        A.addHost(1);
        A.addHost(2);
        A.addHost(3);
        hosts.add(A);
        H B = new H();
        B.addHost(4);
        B.addHost(5);
        B.addHost(6);
        B.addHost(7);
        hosts.add(B);
        H Client = new H();
        Client.addHost(8);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);


        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(3).toIntArray());
        methodsH.put("m2", B.pickFirst(3).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(3), B.pickFirst(3)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(3), B.pickFirst(3)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 2), new P(B, 2)));
        methodsQ.put("m2", new P(B, 2));
        methodsQ.put("m1", new P(A, 2));
        methodsQ.put("ret", new POr(new P(A, 2), new P(B, 2)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    // The number after the host character represents the availability and integrity type
    // (A2B1 means this configuration can withstand upto 2 byzantine nodes in A and 1 node in B
    public void initOT_A2B1(String configuration)
    {
        initializeOT();

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
        hosts.add(A);
        H B = new H();
        B.addHost(7);
        B.addHost(8);
        B.addHost(9);
        B.addHost(10);
        hosts.add(B);
        H Client = new H();
        Client.addHost(11);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(5).toIntArray());
        methodsH.put("m2", B.pickFirst(3).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(5), B.pickFirst(3)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(5), B.pickFirst(3)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 3), new P(B, 2)));
        methodsQ.put("m2", new P(B, 2));
        methodsQ.put("m1", new P(A, 3));
        methodsQ.put("ret", new POr(new P(A, 3), new P(B, 2)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initOT_A3B1(String configuration)
    {
        initializeOT();

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
        A.addHost(7);
        A.addHost(8);
        A.addHost(9);
        hosts.add(A);
        H B = new H();
        B.addHost(10);
        B.addHost(11);
        B.addHost(12);
        B.addHost(13);
        hosts.add(B);
        H Client = new H();
        Client.addHost(14);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(7).toIntArray());
        methodsH.put("m2", B.pickFirst(3).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(7), B.pickFirst(3)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(7), B.pickFirst(3)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 4), new P(B, 2)));
        methodsQ.put("m2", new P(B, 2));
        methodsQ.put("m1", new P(A, 4));
        methodsQ.put("ret", new POr(new P(A, 4), new P(B, 2)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }
    public void initOT_A3B2(String configuration)
    {
        initializeOT();

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
        A.addHost(7);
        A.addHost(8);
        A.addHost(9);
        hosts.add(A);
        H B = new H();
        B.addHost(10);
        B.addHost(11);
        B.addHost(12);
        B.addHost(13);
        B.addHost(14);
        B.addHost(15);
        B.addHost(16);
        hosts.add(B);
        H Client = new H();
        Client.addHost(17);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(7).toIntArray());
        methodsH.put("m2", B.pickFirst(5).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(7), B.pickFirst(5)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(7), B.pickFirst(5)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 4), new P(B, 3)));
        methodsQ.put("m2", new P(B, 3));
        methodsQ.put("m1", new P(A, 4));
        methodsQ.put("ret", new POr(new P(A, 4), new P(B, 3)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initOT_A3B3(String configuration)
    {
        initializeOT();

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
        A.addHost(7);
        A.addHost(8);
        A.addHost(9);
        hosts.add(A);
        H B = new H();
        B.addHost(10);
        B.addHost(11);
        B.addHost(12);
        B.addHost(13);
        B.addHost(14);
        B.addHost(15);
        B.addHost(16);
        B.addHost(17);
        B.addHost(18);
        B.addHost(19);
        hosts.add(B);
        H Client = new H();
        Client.addHost(20);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(7).toIntArray());
        methodsH.put("m2", B.pickFirst(7).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(7), B.pickFirst(7)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(7), B.pickFirst(7)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 4), new P(B, 4)));
        methodsQ.put("m2", new P(B, 4));
        methodsQ.put("m1", new P(A, 4));
        methodsQ.put("ret", new POr(new P(A, 4), new P(B, 4)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initOT_A4B1(String configuration)
    {
        initializeOT();

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
        A.addHost(7);
        A.addHost(8);
        A.addHost(9);
        A.addHost(10);
        A.addHost(11);
        A.addHost(12);
        hosts.add(A);
        H B = new H();
        B.addHost(13);
        B.addHost(14);
        B.addHost(15);
        B.addHost(16);
        hosts.add(B);
        H Client = new H();
        Client.addHost(17);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(9).toIntArray());
        methodsH.put("m2", B.pickFirst(3).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(9), B.pickFirst(3)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(9), B.pickFirst(3)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 5), new P(B, 2)));
        methodsQ.put("m2", new P(B, 2));
        methodsQ.put("m1", new P(A, 5));
        methodsQ.put("ret", new POr(new P(A, 5), new P(B, 2)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initOT_A5B1(String configuration)
    {
        initializeOT();

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
        A.addHost(7);
        A.addHost(8);
        A.addHost(9);
        A.addHost(10);
        A.addHost(11);
        A.addHost(12);
        A.addHost(13);
        A.addHost(14);
        A.addHost(15);
        hosts.add(A);
        H B = new H();
        B.addHost(16);
        B.addHost(17);
        B.addHost(18);
        B.addHost(19);
        hosts.add(B);
        H Client = new H();
        Client.addHost(20);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(11).toIntArray());
        methodsH.put("m2", B.pickFirst(3).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(11), B.pickFirst(3)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(11), B.pickFirst(3)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 6), new P(B, 2)));
        methodsQ.put("m2", new P(B, 2));
        methodsQ.put("m1", new P(A, 6));
        methodsQ.put("ret", new POr(new P(A, 6), new P(B, 2)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initOT_A6B1(String configuration)
    {
        initializeOT();

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
        A.addHost(7);
        A.addHost(8);
        A.addHost(9);
        A.addHost(10);
        A.addHost(11);
        A.addHost(12);
        A.addHost(13);
        A.addHost(14);
        A.addHost(15);
        A.addHost(16);
        A.addHost(17);
        A.addHost(18);
        hosts.add(A);
        H B = new H();
        B.addHost(19);
        B.addHost(20);
        B.addHost(21);
        B.addHost(22);
        hosts.add(B);
        H Client = new H();
        Client.addHost(23);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(13).toIntArray());
        methodsH.put("m2", B.pickFirst(3).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(13), B.pickFirst(3)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(13), B.pickFirst(3)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 7), new P(B, 2)));
        methodsQ.put("m2", new P(B, 2));
        methodsQ.put("m1", new P(A, 7));
        methodsQ.put("ret", new POr(new P(A, 7), new P(B, 2)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initOT_A7B1(String configuration)
    {
        initializeOT();

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
        A.addHost(7);
        A.addHost(8);
        A.addHost(9);
        A.addHost(10);
        A.addHost(11);
        A.addHost(12);
        A.addHost(13);
        A.addHost(14);
        A.addHost(15);
        A.addHost(16);
        A.addHost(17);
        A.addHost(18);
        A.addHost(19);
        A.addHost(20);
        A.addHost(21);
        hosts.add(A);
        H B = new H();
        B.addHost(22);
        B.addHost(23);
        B.addHost(24);
        B.addHost(25);
        hosts.add(B);
        H Client = new H();
        Client.addHost(26);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(15).toIntArray());
        methodsH.put("m2", B.pickFirst(3).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(15), B.pickFirst(3)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(15), B.pickFirst(3)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 8), new P(B, 2)));
        methodsQ.put("m2", new P(B, 2));
        methodsQ.put("m1", new P(A, 8));
        methodsQ.put("ret", new POr(new P(A, 8), new P(B, 2)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initOT_A2B2(String configuration) {
        initializeOT();
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
        hosts.add(A);
        H B = new H();
        B.addHost(7);
        B.addHost(8);
        B.addHost(9);
        B.addHost(10);
        B.addHost(11);
        B.addHost(12);
        B.addHost(13);
        hosts.add(B);
        H Client = new H();
        Client.addHost(14);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);


        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(5).toIntArray());
        methodsH.put("m2", B.pickFirst(5).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(5), B.pickFirst(5)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(5), B.pickFirst(5)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 3), new P(B, 3)));
        methodsQ.put("m2", new P(B, 3));
        methodsQ.put("m1", new P(A, 3));
        methodsQ.put("ret", new POr(new P(A, 3), new P(B, 3)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initOT_A4B4(String configuration) {
        initializeOT();
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
        A.addHost(7);
        A.addHost(8);
        A.addHost(9);
        A.addHost(10);
        A.addHost(11);
        A.addHost(12);
        hosts.add(A);

        H B = new H();
        B.addHost(13);
        B.addHost(14);
        B.addHost(15);
        B.addHost(16);
        B.addHost(17);
        B.addHost(18);
        B.addHost(19);
        B.addHost(20);
        B.addHost(21);
        B.addHost(22);
        B.addHost(23);
        B.addHost(24);
        B.addHost(25);
        hosts.add(B);

        H Client = new H();
        Client.addHost(26);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, Client);


        methodsH = new HashMap<>();
        methodsH.put("m1", A.pickFirst(9).toIntArray());
        methodsH.put("m2", B.pickFirst(9).toIntArray());
        methodsH.put("m3", H.union(A.pickFirst(9), B.pickFirst(9)).toIntArray());
        methodsH.put("m4", H.union(A.pickFirst(9), B.pickFirst(9)).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
        methodsQ.put("m4", new P(Client, 1));
        methodsQ.put("m3", new PAnd(new P(A, 5), new P(B, 5)));
        methodsQ.put("m2", new P(B, 5));
        methodsQ.put("m1", new P(A, 5));
        methodsQ.put("ret", new POr(new P(A, 5), new P(B, 5)));

        objectsQ = new HashMap<>();
        objectsQ.put("i1", new P(A, 2));
        objectsQ.put("i2", new P(B, 2));
        objectsQ.put("a", new POr(new P(A, 2), new P(B, 2)));

        finilaizeOT(configuration, A, B);
    }

    public void initAuction_A2B1(String configuration)
    {
        initializeAuction();

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
        hosts.add(A);

        H B = new H();
        B.addHost(7);
        B.addHost(8);
        B.addHost(9);
        B.addHost(10);
        hosts.add(B);

        H C = new H();
        C.addHost(11);
        hosts.add(C);

        H Client = new H();
        Client.addHost(12);

        allHosts = new H();
        allHosts = H.union(allHosts, A);
        allHosts = H.union(allHosts, B);
        allHosts = H.union(allHosts, C);
        allHosts = H.union(allHosts, Client);

        methodsH = new HashMap<>();
        methodsH.put("m1", C.pickFirst(1).toIntArray());
        methodsH.put("m2", A.pickFirst(5).toIntArray());
        methodsH.put("m3", C.pickFirst(1).toIntArray());
        methodsH.put("m4", C.pickFirst(1).toIntArray());
        methodsH.put("m5", B.pickFirst(3).toIntArray());
        methodsH.put("m6", C.pickFirst(1).toIntArray());
        methodsH.put("ret", Client.pickFirst(1).toIntArray());

        // initialize methods qs. there are three possibilities:
        // 1) Single Q
        // 2) And of two Qs
        // 3) Or of two Qs
        methodsQ = new HashMap<>();
//        methodsQ.put("m1", new PAnd(new P(A, 3), new P(B, 2)));
        methodsQ.put("m1", new POr(new P(Client, 1), new P(C, 1)));
        methodsQ.put("m2", new P(C, 1));
        methodsQ.put("m3", new P(A, 3));
        methodsQ.put("m4", new P(C, 1));
        methodsQ.put("m5", new P(C, 1));
        methodsQ.put("m6", new P(B, 2));
        methodsQ.put("ret", new P(C, 1));

        objectsQ = new HashMap<>();
        objectsQ.put("agentA", new P(A, 3));
        objectsQ.put("agentB", new P(B, 2));
        objectsQ.put("userAgent", new POr(new P(C, 1), new POr(new P(A, 2), new P(B, 3))));

        finilaizeAuction(configuration, A, B, C);
    }

    private void writeSystemConfigFile(H h, int clusterID, String configPath)
    {
        try
        {
            FileReader fr = new FileReader("systemconfig/system.config");
            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            String file = "";
            while ((line = rd.readLine()) != null) {
                if (!line.startsWith("#")) {
                    file += line;
                    file += "\n";
                }
            }
            fr.close();
            rd.close();
            String initView = "";
            for(int i = 0; i < h.size(); i++)
            {
                if(i != h.size()-1)
                    initView += h.toIntArray()[i] + ",";
                else
                    initView += h.toIntArray()[i];
            }
            /* arguments of the template system.config file:
            1) clusterID
            2) number of servers
            3) number of faulty nodes
            4) initial view
             */
            file = String.format(file, clusterID, h.size(), (h.size()-1)/3, initView);
            String sep = System.getProperty("file.separator");
            String fileName = configPath + sep + "system.config" + clusterID;
            if(!new File(fileName).exists()) {
                PrintWriter systemConfigWriter = new PrintWriter(fileName, "UTF-8");
                systemConfigWriter.write(file);
                systemConfigWriter.flush();
            }
        }
        catch (IOException e)
        {
            System.out.println("Cannot read system config template file");
        }
    }

    private void writeHostsConfigFile(H h, int clusterID, String configPath, int basePort, int baseHostID)
    {
        try {
            String sep = System.getProperty("file.separator");
            String fileName = configPath + sep + "hosts.config" + clusterID;
            if(!new File(fileName).exists()) {
                PrintWriter aWriter = new PrintWriter(configPath + sep + "hosts.config" + clusterID, "UTF-8");
                for (int i = 0; i < h.size(); i++) {
                    String hostLine = i + baseHostID + " " + hostipMap.get(i + baseHostID) + " " + String.valueOf(basePort + (10 * i)) + " " + String.valueOf(basePort + 1 + (10 * i)) + " " + String.valueOf(basePort + 2 + (10 * i)) + "\n";
                    aWriter.write(hostLine);
                    aWriter.flush();
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Cannot create hosts.config" + clusterID + " file");
        }
    }


    public void setRuntime(RMIRuntime runtime) {
        this.runtime = runtime;
    }

    public HashMap<String, int[]> getMethodsH() {
        return methodsH;
    }

    public HashMap<String, Q> getMethodsQ() {
        return methodsQ;
    }

    public HashMap<String, Q> getObjectsQ() {
        return objectsQ;
    }

    public HashMap<String, Class[]> getArgsMap() {
        return argsMap;
    }

    public H getAllHosts() {
        return allHosts;
    }

    public ArrayList<H> getHosts() {
        return hosts;
    }
}
