package bftsmart.usecase;

import bftsmart.runtime.quorum.H;
import bftsmart.runtime.quorum.Q;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Spec {
    private boolean local = false;

    private String configurationName;

    private HashMap<String, Configuration> configurations;
    private int hostsSize;

    private HashMap<String,int[]> methodsH = new HashMap<>();

    // The quorum required for the methods to be able to execute
    private HashMap<String, Q> methodsQ = new HashMap<>();

    private HashMap<String, Q> objectsQ = new HashMap<>();

    private HashMap<String, int[]> objectsH = new HashMap<>();

    // A mapping to store the argument type of the all the methods
    private HashMap<String,Class[]> argsMap = new HashMap<>();

    private HashMap<Integer,String> hostsIpMap;

    private HashMap<String,H> principalSets = new HashMap<>();

    public Spec(String configPath, HashMap<Integer,String> hostsMapping) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        configurations = readSpecificationFromFile(configPath);
//        hostsSize = configurations.values().stream().reduce(0, (conf1, conf2) -> conf1.getHostSet().length + conf2.getHostSet().length);
        hostsIpMap = hostsMapping;
        configurationName = configPath.split("/")[configPath.split("/").length-1];

        //initialize
//        initializeAuction();
        initialize(configurations.values());
        // do stuff
        //finalize
//        finilaizeAuction(configurationName, );
        writeConfigsAndFinalize();


        principalSets = createPrincipalSets();
        methodsH = getAllMethodsHosts();
        methodsQ = getMethodsQ();
        objectsH = getObjectsH();
        objectsQ = getObjectsQ();
    }

    public void initialize(Collection<Configuration> configurations) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        argsMap = new HashMap<>();
        for(Configuration conf : configurations) {
            PartitionedObject object = (PartitionedObject) Class.forName(conf.getClassName()).getConstructor().newInstance();
            argsMap.putAll(extractSplittedMethods(object));
            argsMap.putAll(extractObjectFieldMethods(object));
        }
    }

    private HashMap<String,Class[]> extractObjectFieldMethods(PartitionedObject object)
    {
        HashMap<String,Class[]> methodArgs = new HashMap<>();
        for(Field objField : object.getClass().getDeclaredFields())
            for(Method method : objField.getType().getDeclaredMethods())
                methodArgs.put(objField.getName() + "-" + method.getName(), method.getParameterTypes());

        return methodArgs;
    }
    private HashMap<String,Class[]> extractSplittedMethods(PartitionedObject object)
    {
        HashMap<String,Class[]> methodArgs = new HashMap<>();
        for(Method method : object.getClass().getDeclaredMethods())
            methodArgs.put(method.getName(), method.getParameterTypes());

        return methodArgs;
    }

    public void writeConfigsAndFinalize()
    {
        String configPath = "config_" + configurationName;
        File directory = new File(configPath);
        if (! directory.exists()){
            directory.mkdir();
        }

        // replication of agentA
//        writeHostsConfigFile(A, 1, configPath, 11000, 0);
//        writeSystemConfigFile(A, 1, configPath);
//        // replication of agentB
//        writeHostsConfigFile(B, 2, configPath, 12000, A.size());
//        writeSystemConfigFile(B, 2, configPath);
//        // replication of userAgent
//        writeHostsConfigFile(C, 3, configPath, 13000, A.size()+B.size());
//        writeSystemConfigFile(C, 3, configPath);


        String runtimeConfigPath = "runtimeconfig_" + configurationName;
        directory = new File(runtimeConfigPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // create runtime configuration
        writeHostsConfigFile(getAllHosts(), 1, runtimeConfigPath, 14000, 0);
        writeSystemConfigFile(getAllHosts(), 1, runtimeConfigPath);
    }

    private HashMap<String, Configuration> readSpecificationFromFile(String configpath)
    {
        HashMap<String,Configuration> configs = new HashMap<>();
        try
        {
            FileReader fr = new FileReader(configpath);
            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            while ((line = rd.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String hostsSetName = line.split("\\s+")[0];
                    String partitionedClassName = line.split("\\s+")[1];
                    String hostList = line.split("\\s+")[2];
                    String clusterId = line.split("\\s+")[3];
                    Configuration c = new Configuration(hostsSetName, partitionedClassName, hostList, clusterId);
                    configs.put(hostsSetName, c);
                }
            }
            fr.close();
            rd.close();
        }
        catch (IOException e)
        {
            System.out.println("Cannot read use-case config file");
        }
        return configs;

    }

    private HashMap<String,H> createPrincipalSets()
    {
        HashMap<String,H> hosts = new HashMap<>();
        for(Map.Entry<String,Configuration> sets : configurations.entrySet())
        {
            H h = new H(sets.getKey());
            for (int p : sets.getValue().getHostSet())
                h.addHost(p);
            hosts.put(sets.getKey(),h);
        }
        return hosts;
    }

    //TODO this needs to change later (don't have access to class code of the other hosts)
    private HashMap<String,int[]> getAllMethodsHosts()
    {
        HashMap<String,int[]> methodsHosts = new HashMap<>();
        try{

            for(Configuration c : configurations.values())
            {
                PartitionedObject object = (PartitionedObject) Class.forName(c.getClassName()).getConstructor().newInstance();
                Method[] methods = object.getClass().getMethods();
                for(Method m : methods) {
                        methodsHosts.put(m.getName(), H.union(methodsHosts.get(m.getName()), c.getHostSet()).toIntArray());
                }
            }
            return methodsHosts;
        }
        catch (SecurityException | ClassNotFoundException | NoSuchMethodException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
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
                    String hostLine = i + baseHostID + " " + hostsIpMap.get(i + baseHostID) + " " + String.valueOf(basePort + (10 * i)) + " " + String.valueOf(basePort + 1 + (10 * i)) + " " + String.valueOf(basePort + 2 + (10 * i)) + "\n";
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

    public HashMap<String, int[]> getObjectsH() {
        return objectsH;
    }

    public HashMap<String, Configuration> getConfigurations() {
        return configurations;
    }

    public H getAllHosts()
    {
        H hs = new H("all");
        for (H h : principalSets.values())
            hs = H.union(hs, h);
        return hs;
    }
}


