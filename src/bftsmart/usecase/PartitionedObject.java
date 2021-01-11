package bftsmart.usecase;
import bftsmart.runtime.RMIRuntime;
import bftsmart.runtime.quorum.H;
import bftsmart.runtime.quorum.Q;
import bftsmart.runtime.quorum.QAnd;
import bftsmart.runtime.quorum.QOr;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class PartitionedObject {
    protected RMIRuntime runtime;

    private HashMap<Integer,String> hostipMap;

    protected int sequenceNumber = 0;

    protected ReentrantLock objCallLock = new ReentrantLock();

    // The id of the processes that host the methods in the given partitioned class
    private HashMap<String,int[]> methodsH;

    // The quorum required for the methods to be able to execute
    private HashMap<String,Q> methodsQ;

    // A mapping to store the argument type of the all the methods
    private HashMap<String,Class[]> argsMap;

    private H allHosts;

    private ArrayList<H> hosts = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        new PartitionedObject();
    }

//    actual oblivious transfer usecase
    public PartitionedObject() {
        try {
            if(RMIRuntime.CONFIGURATION.equals("(A:1;B:1)"))
                initOT_A1B1("(A:1;B:1)");
            else if(RMIRuntime.CONFIGURATION.equals("(A:2;B:1)"))
                initOT_A2B1("(A:2;B:1)");
            else if(RMIRuntime.CONFIGURATION.equals("(A:2;B:2)"))
                initOT_A2B2("(A:2;B:2)");
            else if(RMIRuntime.CONFIGURATION.equals("(A:3;B:1)"))
                initOT_A3B1("(A:3;B:1)");
            else if(RMIRuntime.CONFIGURATION.equals("(A:3;B:2)"))
                initOT_A3B2("(A:3;B:2)");
            else if(RMIRuntime.CONFIGURATION.equals("(A:3;B:3)"))
                initOT_A3B3("(A:3;B:3)");
            else if(RMIRuntime.CONFIGURATION.equals("(A:4;B:1)"))
                initOT_A4B1("(A:4;B:1)");
            else if(RMIRuntime.CONFIGURATION.equals("(A:4;B:4)"))
                initOT_A4B4("(A:4;B:4)");
            else if(RMIRuntime.CONFIGURATION.equals("(A:1;B:1;C:1)"))
                initMax_A1B1C1("(A:1;B:1;C:1)");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void initializeOT()
    {
        argsMap = new HashMap<>();
        argsMap.put("m4", new Class[]{String.class,Integer.class,Integer.class});
        argsMap.put("m3", new Class[]{String.class,Integer.class,Integer.class});
        argsMap.put("m2", new Class[]{String.class,Integer.class});
        argsMap.put("m1", new Class[]{String.class,Integer.class});

        // always here
        argsMap.put("ret", new Class[]{Integer.class});

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
        String configPath = "config" + configuration;
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


        String runtimeConfigPath = "runtime" + configPath;
        directory = new File(runtimeConfigPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // create runtime configuration
        writeHostsConfigFile(allHosts, 1, runtimeConfigPath, 13000, 0);
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
        String configPath = "config" + configuration;
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


        String runtimeConfigPath = "runtime" + configPath;
        directory = new File(runtimeConfigPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // create runtime configuration
        writeHostsConfigFile(allHosts, 1, runtimeConfigPath, 13000, 0);
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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new Q(A, 2));
        methodsQ.put("m2", new Q(B, 2));
        methodsQ.put("ret", new Q(C, 2));

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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 2), new Q(B, 2)));
        methodsQ.put("m2", new Q(B, 2));
        methodsQ.put("m1", new Q(A, 2));
        methodsQ.put("ret", new QOr(new Q(A, 2), new Q(B, 2)));

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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 3), new Q(B, 2)));
        methodsQ.put("m2", new Q(B, 2));
        methodsQ.put("m1", new Q(A, 3));
        methodsQ.put("ret", new QOr(new Q(A, 3), new Q(B, 2)));

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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 4), new Q(B, 2)));
        methodsQ.put("m2", new Q(B, 2));
        methodsQ.put("m1", new Q(A, 4));
        methodsQ.put("ret", new QOr(new Q(A, 4), new Q(B, 2)));

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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 4), new Q(B, 3)));
        methodsQ.put("m2", new Q(B, 3));
        methodsQ.put("m1", new Q(A, 4));
        methodsQ.put("ret", new QOr(new Q(A, 4), new Q(B, 3)));

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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 4), new Q(B, 4)));
        methodsQ.put("m2", new Q(B, 4));
        methodsQ.put("m1", new Q(A, 4));
        methodsQ.put("ret", new QOr(new Q(A, 4), new Q(B, 4)));

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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 5), new Q(B, 2)));
        methodsQ.put("m2", new Q(B, 2));
        methodsQ.put("m1", new Q(A, 5));
        methodsQ.put("ret", new QOr(new Q(A, 5), new Q(B, 2)));

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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 3), new Q(B, 3)));
        methodsQ.put("m2", new Q(B, 3));
        methodsQ.put("m1", new Q(A, 3));
        methodsQ.put("ret", new QOr(new Q(A, 3), new Q(B, 3)));

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
        methodsQ.put("m4", new Q(Client, 1));
        methodsQ.put("m3", new QAnd(new Q(A, 5), new Q(B, 5)));
        methodsQ.put("m2", new Q(B, 5));
        methodsQ.put("m1", new Q(A, 5));
        methodsQ.put("ret", new QOr(new Q(A, 5), new Q(B, 5)));

        finilaizeOT(configuration, A, B);
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
            PrintWriter systemConfigWriter = new PrintWriter(configPath + sep + "system.config" + clusterID, "UTF-8");
            systemConfigWriter.write(file);
            systemConfigWriter.flush();
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
            PrintWriter aWriter = new PrintWriter(configPath + sep + "hosts.config" + clusterID, "UTF-8");
            for(int i = 0; i < h.size(); i++)
            {
                String hostLine = i+baseHostID + " " + hostipMap.get(i+baseHostID) + " " + String.valueOf(basePort + (10*i)) + " " + String.valueOf(basePort+1 + (10*i)) + " " + String.valueOf(basePort+2 + (10*i)) + "\n";
                aWriter.write(hostLine);
                aWriter.flush();
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

    public HashMap<String, Class[]> getArgsMap() {
        return argsMap;
    }

    public H getAllHosts() {
        return allHosts;
    }

    public ArrayList<H> getHosts() {
        return hosts;
    }

    public HashMap<Integer, String> getHostipMap() {
        if(hostipMap == null || hostipMap.size() == 0)
        {
            throw new RuntimeException("hostip map is undefined");
        }
        return hostipMap;
    }

    public void setHostipMap(HashMap<Integer, String> hostipMap) {
        this.hostipMap = hostipMap;
    }

    //    public void initTestOT()
//    {
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
}
