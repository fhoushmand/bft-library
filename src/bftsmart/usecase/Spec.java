package bftsmart.usecase;

import bftsmart.runtime.quorum.H;
import bftsmart.runtime.quorum.P;
import bftsmart.runtime.quorum.Q;
import bftsmart.runtime.util.IntIntPair;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Spec {
    private boolean isLocal;
    private ResiliencyConfiguration resiliencyConfiguration;
    private HashMap<String, Configuration> configurations;
    private int hostsSize;

    // A mapping to store the argument type of the all the methods
    private HashMap<String,Class[]> argsMap;
    // A mapping from object fields to its cluster id
    private HashMap<String, Map.Entry<Class,Integer>> objectFields;

    private HashMap<String, H> objectsH;
    private HashMap<String,H> methodsH;

    // The quorum required for the methods to be able to execute
    private HashMap<String, Q> methodsQ;

    private HashMap<String, Q> objectsQ;

    private HashMap<Integer,String> hostsIpMap = new HashMap<>();

    int clusterIDSequence = 0;


    public Spec(boolean local, String configPath, String[] hostsList) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        isLocal = local;
        configurations = readSpecificationFromFile(configPath);
        hostsSize = configurations.values().stream().reduce(0, (size, config) -> size += config.getHostSet().size(), Integer::sum);
        if(isLocal) {
            for (int i = 0; i < hostsSize; i++) {
                hostsIpMap.put(i, "127.0.0.1");
            }
        }
        else
        {
            int i = 0;
            for (String hostName : hostsList){
                String h = hostName + ".ib.hpcc.ucr.edu";
                hostsIpMap.put(i++, h);
            }
        }
        resiliencyConfiguration = new ResiliencyConfiguration(configPath.split("/")[configPath.split("/").length-1]);
        methodsQ = getMethodsQ();
        objectsQ = getObjectsQ();

        //initialize
        initialize(configurations.values());
        // specify object and method communication quorums
        //finalize
        writeConfigsAndFinalize();
    }

    public void initialize(Collection<Configuration> configurations)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        argsMap = new HashMap<>();
        objectFields = new HashMap<>();
        objectsH = new HashMap<>();
        methodsH = new HashMap<>();
        for(Configuration conf : configurations) {
            PartitionedObject object = (PartitionedObject) Class.forName(conf.getClassName()).getConstructor().newInstance();
            extractSplitMethodsArgs(object);
            extractObjectFieldMethods(object);
            extractObjectPlacements(object);
            extractMethodHosts(object);
        }
        getMethodCommunicationQuorum();
        getObjectCommunicationQuorum();
    }

    private void extractObjectFieldMethods(PartitionedObject object)
    {
        for(Field objField : object.getClass().getDeclaredFields())
            for(Method method : objField.getType().getDeclaredMethods())
                argsMap.put(objField.getName() + "-" + method.getName(), method.getParameterTypes());
    }
    private void extractSplitMethodsArgs(PartitionedObject object)
    {
        for(Method method : object.getClass().getDeclaredMethods())
            argsMap.put(method.getName(), method.getParameterTypes());
    }

    private void extractObjectPlacements(PartitionedObject object)
    {
        for(Field objField : object.getClass().getDeclaredFields()) {
            objectFields.put(objField.getName(), Map.entry(objField.getType(), clusterIDSequence++));
            H hosts = getHostByPartitionedClass(object);
            if(objectsH.containsKey(objField.getName()))
                objectsH.put(objField.getName(), H.union(objectsH.get(objField.getName()), hosts));
            else
                objectsH.put(objField.getName(), hosts);
        }
    }

    private void extractMethodHosts(PartitionedObject object)
    {
        for(Method m : object.getClass().getDeclaredMethods()) {
            H allHosts = getHostByPartitionedClass(object);
            H requiredHosts = allHosts.pickFirst(2 * resiliencyConfiguration.getPrincipalResiliency(getPrincipalNameByPartitionedClass(object)) + 1);
            if(methodsH.containsKey(m.getName()))
                methodsH.put(m.getName(), H.union(methodsH.get(m.getName()), requiredHosts));
            else
                methodsH.put(m.getName(), requiredHosts);
        }
    }

    private void getMethodCommunicationQuorum()
    {
        methodsQ = new HashMap<>();
        methodsQ.put("m1", new P(configurations.get("Client").getHostSet(), 1));
        methodsQ.put("ret", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
    }

    private void getObjectCommunicationQuorum()
    {
        objectsQ = new HashMap<>();
        objectsQ.put("r1", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
        objectsQ.put("r2", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
        objectsQ.put("r", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
    }

    public void writeConfigsAndFinalize()
    {
        String configPath = "config_" + resiliencyConfiguration;
        File directory = new File(configPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        int basePort = 15000;
        for(Map.Entry<String,H> placement : objectsH.entrySet())
        {
            int clusterID = getClusterIDByObjectField(placement.getKey());
            writeHostsConfigFile(placement.getValue(), clusterID, configPath, basePort);
            writeSystemConfigFile(placement.getValue(), clusterID, configPath);
            basePort+=1000;
        }


        String runtimeConfigPath = "runtimeconfig_" + resiliencyConfiguration;
        directory = new File(runtimeConfigPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // create runtime configuration
        writeHostsConfigFile(getAllHosts(), 1, runtimeConfigPath, basePort);
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

    private void writeHostsConfigFile(H h, int clusterID, String configPath, int basePort)
    {
        int baseHostID = h.toIntArray()[0];
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


    public HashMap<String, H> getMethodsH() {
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

    public HashMap<String, H> getObjectsH() {
        return objectsH;
    }

    public HashMap<String, Configuration> getConfigurations() {
        return configurations;
    }

    public HashMap<String, Map.Entry<Class, Integer>> getObjectFields() {
        return objectFields;
    }

    public H getAllHosts()
    {
        H hs = new H("all");
        for (Map.Entry<String,Configuration> config : configurations.entrySet())
            hs = H.union(hs, config.getValue().getHostSet());
        return hs;
    }

    public String getPrincipalNameByPartitionedClass(PartitionedObject object)
    {
        for (Map.Entry<String,Configuration> config : configurations.entrySet())
        {
            if(config.getValue().getClassName().equals(object.getClass().getName()))
                return config.getValue().getPrincipalName();
        }
        return null;
    }

    public Class getObjectFieldTypeByName(String name)
    {
        return objectFields.get(name).getKey();
    }

    public H getHostByPartitionedClass(PartitionedObject object)
    {
        for (Map.Entry<String,Configuration> config : configurations.entrySet())
        {
            if(config.getValue().getClassName().equals(object.getClass().getName()))
                return config.getValue().getHostSet();
        }
        return null;
    }

    public String getPartitionedClassByHostID(Integer id)
    {
        for (Map.Entry<String,Configuration> config : configurations.entrySet())
        {
            for(int h : config.getValue().getHostSet().toIntArray())
            {
                if(h == id)
                    return config.getValue().getClassName();
            }
        }
        return null;
    }

    public Integer getClusterIDByObjectField(String name)
    {
        return objectFields.get(name).getValue();
    }
}


