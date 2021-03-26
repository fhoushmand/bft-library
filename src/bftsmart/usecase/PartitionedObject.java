package bftsmart.usecase;
import bftsmart.runtime.RMIRuntime;
import bftsmart.runtime.quorum.*;
import bftsmart.runtime.quorum.P;
import bftsmart.runtime.quorum.PAnd;
import bftsmart.runtime.util.IntIntPair;
import bftsmart.usecase.auction.OfferInfo;
import bftsmart.usecase.ticket.TicketInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PartitionedObject {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public int responseReceived = 0;
    public ReentrantLock objCallLock = new ReentrantLock();
    public Condition requestBlock = objCallLock.newCondition();

    protected RMIRuntime runtime;
    public int sequenceNumber = 0;

    public void setRuntime(RMIRuntime runtime) {
        this.runtime = runtime;
    }


    public PartitionedObject() { }

//    public void initializeOT()
//    {
//        argsMap = new HashMap<>();
//        argsMap.put("m4", new Class[]{String.class,Integer.class,Integer.class});
//        argsMap.put("m3", new Class[]{String.class,Integer.class,Integer.class});
//        argsMap.put("m2", new Class[]{String.class,Integer.class});
//        argsMap.put("m1", new Class[]{String.class,Integer.class});
//
//        // always here
//        argsMap.put("request", new Class[]{Integer.class});
//        argsMap.put("ret", new Class[]{String.class,Integer.class,Integer.class});
//
//        //object fields methods
//        argsMap.put("i1-read", new Class[]{String.class});
//        argsMap.put("i2-read", new Class[]{String.class});
//        argsMap.put("a-read", new Class[]{String.class});
//        argsMap.put("i1-write", new Class[]{Integer.class,String.class});
//        argsMap.put("i2-write", new Class[]{Integer.class,String.class});
//        argsMap.put("a-write", new Class[]{Boolean.class,String.class});
//    }
//
//    public void finilaizeOT(String configuration, H A, H B)
//    {
//        // create and write to hosts.config files
//        String configPath = "config_" + configuration;
//        File directory = new File(configPath);
//        if (! directory.exists()){
//            directory.mkdir();
//        }
//        // replication of i1
//        writeHostsConfigFile(A, 1, configPath, 11000, 0);
//        writeSystemConfigFile(A, 1, configPath);
//        // replication of i2
//        writeHostsConfigFile(B, 2, configPath, 12000, A.size());
//        writeSystemConfigFile(B, 2, configPath);
//        // replication of a
//        writeHostsConfigFile(H.union(A, B), 3, configPath, 13000, 0);
//        writeSystemConfigFile(H.union(A,B), 3, configPath);
//
//
//        String runtimeConfigPath = "runtimeconfig_" + configuration;
//        directory = new File(runtimeConfigPath);
//        if (! directory.exists()){
//            directory.mkdir();
//        }
//        // create runtime configuration
//        writeHostsConfigFile(getAllHosts(), 1, runtimeConfigPath, 14000, 0);
//        writeSystemConfigFile(getAllHosts(), 1, runtimeConfigPath);
//    }
//
//    public void initializeAuction()
//    {
//        argsMap = new HashMap<>();
//        argsMap.put("m6", new Class[]{String.class,Integer.class,OfferInfo.class,Integer.class});
//        argsMap.put("m5", new Class[]{String.class,Integer.class,Integer.class,Integer.class});
//        argsMap.put("m4", new Class[]{String.class,Integer.class,Integer.class});
//        argsMap.put("m3", new Class[]{String.class,Integer.class,Integer.class,Integer.class, OfferInfo.class});
//        argsMap.put("m2", new Class[]{String.class,Integer.class,Integer.class,Integer.class});
//        argsMap.put("m1", new Class[]{String.class,Integer.class,Integer.class});
//
//        // always here
//        argsMap.put("request", new Class[]{Integer.class});
//        argsMap.put("ret", new Class[]{String.class,Integer.class,OfferInfo.class});
//
//        //object fields methods
//        argsMap.put("userAgent-read", new Class[]{String.class});
//        argsMap.put("userAgent-updateOffer", new Class[]{OfferInfo.class,String.class});
//        argsMap.put("userAgent-declareWinner", new Class[]{Integer.class,String.class});
//        argsMap.put("agentA-makeOfferA", new Class[]{Integer.class,Integer.class,String.class});
//        argsMap.put("agentB-makeOfferB", new Class[]{Integer.class,Integer.class,String.class});
//    }
//
//    public void finilaizeAuction(String configuration, H A, H B, H C)
//    {
//        // create and write to hosts.config files
//        String configPath = "config_" + configuration;
//        File directory = new File(configPath);
//        if (! directory.exists()){
//            directory.mkdir();
//        }
//        // replication of agentA
//        writeHostsConfigFile(A, 1, configPath, 11000, 0);
//        writeSystemConfigFile(A, 1, configPath);
//        // replication of agentB
//        writeHostsConfigFile(B, 2, configPath, 12000, A.size());
//        writeSystemConfigFile(B, 2, configPath);
//        // replication of userAgent
//        writeHostsConfigFile(C, 3, configPath, 13000, A.size()+B.size());
//        writeSystemConfigFile(C, 3, configPath);
//
//
//        String runtimeConfigPath = "runtimeconfig_" + configuration;
//        directory = new File(runtimeConfigPath);
//        if (! directory.exists()){
//            directory.mkdir();
//        }
//        // create runtime configuration
//        writeHostsConfigFile(getAllHosts(), 1, runtimeConfigPath, 14000, 0);
//        writeSystemConfigFile(getAllHosts(), 1, runtimeConfigPath);
//    }
//
//    public void initializeOblTransfer()
//    {
//        argsMap = new HashMap<>();
//        argsMap.put("m1", new Class[]{String.class,Integer.class,Integer.class});
//
//        // always here
//        argsMap.put("request", new Class[]{Integer.class});
//        argsMap.put("ret", new Class[]{String.class,Integer.class,Integer.class});
//
//        //object fields methods
//        argsMap.put("r1-read", new Class[]{String.class});
//        argsMap.put("r2-read", new Class[]{String.class});
//        argsMap.put("r-read", new Class[]{String.class});
//        argsMap.put("r-write", new Class[]{Boolean.class,String.class});
//    }
//
//    public void finilaizeOblTransfer(String configuration, H A, H B, H C)
//    {
//        // create and write to hosts.config files
//        String configPath = "config_" + configuration;
//        File directory = new File(configPath);
//        if (! directory.exists()){
//            directory.mkdir();
//        }
//        // replication of r1
//        writeHostsConfigFile(A, 1, configPath, 11000, 0);
//        writeSystemConfigFile(A, 1, configPath);
//        // replication of r2
//        writeHostsConfigFile(B, 2, configPath, 12000, A.size());
//        writeSystemConfigFile(B, 2, configPath);
//        // replication of r
//        writeHostsConfigFile(H.union(A,B), 3, configPath, 13000, 0);
//        writeSystemConfigFile(H.union(A,B), 3, configPath);
//
//
//        String runtimeConfigPath = "runtimeconfig_" + configuration;
//        directory = new File(runtimeConfigPath);
//        if (! directory.exists()){
//            directory.mkdir();
//        }
//        // create runtime configuration
//        writeHostsConfigFile(getAllHosts(), 1, runtimeConfigPath, 14000, 0);
//        writeSystemConfigFile(getAllHosts(), 1, runtimeConfigPath);
//    }
//
//    public void initializeTicket()
//    {
//        argsMap = new HashMap<>();
//        argsMap.put("m8", new Class[]{String.class,Integer.class,Integer.class});
//        argsMap.put("m7", new Class[]{String.class,Integer.class,Integer.class});
//        argsMap.put("m6", new Class[]{String.class,Integer.class, IntIntPair.class,Integer.class});
//        argsMap.put("m5", new Class[]{String.class,Integer.class,Integer.class});
////        argsMap.put("m4", new Class[]{String.class,Integer.class,Integer.class});
//        argsMap.put("m3", new Class[]{String.class,Integer.class,TicketInfo.class});
//        argsMap.put("m2", new Class[]{String.class,Integer.class,Integer.class});
////        argsMap.put("m1", new Class[]{String.class,Integer.class});
//
//        // always here
//        argsMap.put("request", new Class[]{});
//        argsMap.put("ret", new Class[]{String.class,Integer.class,Boolean.class});
//
//        //object fields methods
////        argsMap.put("userAgent-read", new Class[]{String.class});
////        argsMap.put("userAgent-ticketNum", new Class[]{String.class});
//        argsMap.put("userAgent-updateInfo", new Class[]{TicketInfo.class,String.class});
//        argsMap.put("userAgent-updatePayment", new Class[]{IntIntPair.class,String.class});
//
//        argsMap.put("bankAgent-getBalance", new Class[]{String.class});
//        argsMap.put("bankAgent-decBalance", new Class[]{Integer.class,String.class});
//
//        argsMap.put("airlineAgent-getPrice", new Class[]{Integer.class,String.class});
//        argsMap.put("airlineAgent-decSeat", new Class[]{String.class});
//    }
//
//    public void finilaizeTicket(String configuration, H A, H B, H C)
//    {
//        // create and write to hosts.config files
//        String configPath = "config_" + configuration;
//        File directory = new File(configPath);
//        if (! directory.exists()){
//            directory.mkdir();
//        }
//        // replication of airline
//        writeHostsConfigFile(A, 1, configPath, 11000, 0);
//        writeSystemConfigFile(A, 1, configPath);
//        // replication of bank
//        writeHostsConfigFile(B, 2, configPath, 12000, A.size());
//        writeSystemConfigFile(B, 2, configPath);
//        // replication of userAgent
//        writeHostsConfigFile(C, 3, configPath, 13000, A.size()+B.size());
//        writeSystemConfigFile(C, 3, configPath);
//
//
//        String runtimeConfigPath = "runtimeconfig_" + configuration;
//        directory = new File(runtimeConfigPath);
//        if (! directory.exists()){
//            directory.mkdir();
//        }
//        // create runtime configuration
//        writeHostsConfigFile(getAllHosts(), 1, runtimeConfigPath, 14000, 0);
//        writeSystemConfigFile(getAllHosts(), 1, runtimeConfigPath);
//    }
//
//
//    // The number after the host character represents the availability and integrity type
//    // (A2B1 means this configuration can withstand upto 2 byzantine nodes in A and 1 node in B
//    public void initOT_A2B1(String configuration)
//    {
//        initializeOT();
//
//        //initialize the list host sets
//        //TODO pass this information as argument
//        H A = new H();
//        A.addHost(0);
//        A.addHost(1);
//        A.addHost(2);
//        A.addHost(3);
//        A.addHost(4);
//        A.addHost(5);
//        A.addHost(6);
//
//        H B = new H();
//        B.addHost(7);
//        B.addHost(8);
//        B.addHost(9);
//        B.addHost(10);
//
//        H Client = new H();
//        Client.addHost(11);
//
//        methodsH = new HashMap<>();
//        methodsH.put("m1", A.pickFirst(5).toIntArray());
//        methodsH.put("m2", B.pickFirst(3).toIntArray());
//        methodsH.put("m3", H.union(A.pickFirst(5), B.pickFirst(3)).toIntArray());
//        methodsH.put("m4", H.union(A.pickFirst(5), B.pickFirst(3)).toIntArray());
//        methodsH.put("ret", Client.pickFirst(1).toIntArray());
//
//        // initialize methods qs. there are three possibilities:
//        // 1) Single Q
//        // 2) And of two Qs
//        // 3) Or of two Qs
//        methodsQ = new HashMap<>();
//        methodsQ.put("m4", new P(Client, 1));
//        methodsQ.put("m3", new PAnd(new P(A, 3), new P(B, 2)));
//        methodsQ.put("m2", new P(B, 2));
//        methodsQ.put("m1", new P(A, 3));
//        methodsQ.put("ret", new POr(new P(A, 3), new P(B, 2)));
//
//        objectsQ = new HashMap<>();
//        objectsQ.put("i1", new P(A, 3));
//        objectsQ.put("i2", new P(B, 2));
//        objectsQ.put("a", new POr(new P(A, 3), new P(B, 2)));
//
//        finilaizeOT(configuration, A, B);
//    }
//
//
//    public void initAuction_A2B1C0(String configuration)
//    {
//        initializeAuction();
//
//        //initialize the list host sets
//        //TODO pass this information as argument
//        H A = new H();
//        A.addHost(0);
//        A.addHost(1);
//        A.addHost(2);
//        A.addHost(3);
//        A.addHost(4);
//        A.addHost(5);
//        A.addHost(6);
//
//        H B = new H();
//        B.addHost(7);
//        B.addHost(8);
//        B.addHost(9);
//        B.addHost(10);
//        H C = new H();
//        C.addHost(11);
//
//        H Client = new H();
//        Client.addHost(12);
//
//        methodsH = new HashMap<>();
//        methodsH.put("m1", C.pickFirst(1).toIntArray());
//        methodsH.put("m2", A.pickFirst(5).toIntArray());
//        methodsH.put("m3", C.pickFirst(1).toIntArray());
//        methodsH.put("m4", C.pickFirst(1).toIntArray());
//        methodsH.put("m5", B.pickFirst(3).toIntArray());
//        methodsH.put("m6", C.pickFirst(1).toIntArray());
//        methodsH.put("ret", Client.pickFirst(1).toIntArray());
//
//        // initialize methods qs. there are three possibilities:
//        // 1) Single Q
//        // 2) And of two Qs
//        // 3) Or of two Qs
//        methodsQ = new HashMap<>();
//        methodsQ.put("m1", new POr(new P(Client, 1), new P(C, 1)));
//        methodsQ.put("m2", new P(C, 1));
//        methodsQ.put("m3", new P(A, 3));
//        methodsQ.put("m4", new P(C, 1));
//        methodsQ.put("m5", new P(C, 1));
//        methodsQ.put("m6", new P(B, 2));
//        methodsQ.put("ret", new P(C, 1));
//
//        objectsQ = new HashMap<>();
//        objectsQ.put("agentA", new P(A, 3));
//        objectsQ.put("agentB", new P(B, 2));
//        objectsQ.put("userAgent", new POr(new P(C, 1), new POr(new P(A, 3), new P(B, 2))));
//
//        finilaizeAuction(configuration, A, B, C);
//    }
//
//    public void initTicket_A2B2C0(String configuration)
//    {
//        initializeTicket();
//
//        //initialize the list host sets
//        //TODO pass this information as argument
//        H A = new H();
//        A.addHost(0);
//        A.addHost(1);
//        A.addHost(2);
//        A.addHost(3);
//        A.addHost(4);
//        A.addHost(5);
//        A.addHost(6);
//
//        H B = new H();
//        B.addHost(7);
//        B.addHost(8);
//        B.addHost(9);
//        B.addHost(10);
//        B.addHost(11);
//        B.addHost(12);
//        B.addHost(13);
//
//        H C = new H();
//        C.addHost(14);
//
//        H Client = new H();
//        Client.addHost(15);
//
//        methodsH = new HashMap<>();
//        methodsH.put("m2", A.pickFirst(5).toIntArray());
//        methodsH.put("m3", C.pickFirst(1).toIntArray());
//        methodsH.put("m5", B.pickFirst(5).toIntArray());
//        methodsH.put("m6", C.pickFirst(1).toIntArray());
//        methodsH.put("m7", A.pickFirst(5).toIntArray());
//        methodsH.put("m8", B.pickFirst(5).toIntArray());
//        methodsH.put("ret", Client.pickFirst(1).toIntArray());
//
//        // initialize methods qs. there are three possibilities:
//        // 1) Single Q
//        // 2) And of two Qs
//        // 3) Or of two Qs
//        methodsQ = new HashMap<>();
//        methodsQ.put("m2", new P(Client, 1));
//        methodsQ.put("m3", new P(A, 3));
//        methodsQ.put("m5", new P(C, 1));
//        methodsQ.put("m6", new P(B, 3));
//        methodsQ.put("m7", new P(C, 1));
//        methodsQ.put("m8", new P(A, 3));
//        methodsQ.put("ret", new POr(new P(C, 1), new P(B, 3)));
//
//        objectsQ = new HashMap<>();
//        objectsQ.put("airlineAgent", new P(A, 3));
//        objectsQ.put("bankAgent", new P(B, 3));
//        objectsQ.put("userAgent", new POr(new P(C, 1), new POr(new P(A, 3), new P(B, 3))));
//
//        finilaizeTicket(configuration, A, B, C);
//    }
//
//    public void initOblTransfer_A1B1C2(String configuration)
//    {
//        initializeOblTransfer();
//
//        //initialize the list host sets
//        //TODO pass this information as argument
//        H A = new H();
//        A.addHost(0);
//        A.addHost(1);
//        A.addHost(2);
//        A.addHost(3);
//
//        H B = new H();
//        B.addHost(4);
//        B.addHost(5);
//        B.addHost(6);
//        B.addHost(7);
//
//        H C = new H();
//        C.addHost(8);
//        C.addHost(9);
//        C.addHost(10);
//        C.addHost(11);
//        C.addHost(12);
//        C.addHost(13);
//        C.addHost(14);
//
//        H Client = new H();
//        Client.addHost(15);
//
//        methodsH = new HashMap<>();
//        methodsH.put("m1", C.pickFirst(5).toIntArray());
//        methodsH.put("ret", Client.pickFirst(1).toIntArray());
//
//        // initialize methods qs. there are three possibilities:
//        // 1) Single Q
//        // 2) And of two Qs
//        // 3) Or of two Qs
//        methodsQ = new HashMap<>();
//        methodsQ.put("m1", new P(Client, 1));
//        methodsQ.put("ret", new P(C, 3));
//
//
//        objectsH = new HashMap<>();
//        objectsH.put("r1", A.toIntArray());
//        objectsH.put("r2", B.toIntArray());
//        objectsH.put("r", H.union(A,B).toIntArray());
//
//        objectsQ = new HashMap<>();
//        objectsQ.put("r1", new P(C, 3));
//        objectsQ.put("r2", new P(C, 3));
//        objectsQ.put("r", new P(C, 3));
//        finilaizeOblTransfer(configuration, A, B, C);
//    }
}
