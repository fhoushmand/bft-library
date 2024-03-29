package bftsmart.usecase;

import bftsmart.runtime.RMIRuntime;
import bftsmart.runtime.quorum.*;
import bftsmart.runtime.util.IntIntPair;
import com.yahoo.ycsb.generator.IntegerGenerator;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

public class Spec {
    private boolean isLocal;
    private boolean injectFault = true;
    private ResiliencyConfiguration resiliencyConfiguration;
    //private String resiliencyStr;
    private HashMap<String, Configuration> configurations;
    private int hostsSize;
    private int n; //the number of the host domain.

    private String useCaseName;

    // A mapping to store the argument type of the all the methods
    private HashMap<String,Class[]> argsMap;
    // A mapping from object fields to its cluster id
    private HashMap<String, Map.Entry<Class,Integer>> objectFields;

    //the hosts for objects and methods
    private HashMap<String, H> objectsH;
    private HashMap<String,H> methodsH;

    //The quorum required for the methods to be able to execute
    //the communication quorum for methods and objects
    private HashMap<String, Q> methodsQ;
    private HashMap<String, Q> objectsQ;

    private HashMap<Integer,String> hostsIpMap = new HashMap<>();

    int clusterIDSequence = 0;


    public Spec(boolean local, String configPath, String[] hostsList) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        isLocal = local;
        objectsH = new HashMap<>();
        methodsH = new HashMap<>();
        objectsQ = new HashMap<>();
        methodsQ = new HashMap<>();
        configurations =  new HashMap<>();
        //configurations = readSpecificationFromFile(configPath);
        readSpecificationFromFile(configPath);
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
        //resiliencyStr = configPath.split("/")[configPath.split("/").length-1];
        resiliencyConfiguration = new ResiliencyConfiguration(configPath.split("/")[configPath.split("/").length-1]);
        useCaseName = configPath.split("/")[configPath.split("/").length-2];
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
        //objectsH = new HashMap<>();
        //methodsH = new HashMap<>();
        for(Configuration conf : configurations) {
            PartitionedObject object = (PartitionedObject) Class.forName(conf.getClassName()).getConstructor().newInstance();
            extractSplitMethodsArgs(object);
            extractObjectFieldMethods(object);
            extractObjectPlacements(object);
            //extractMethodHosts(object);
        }
        //getMethodCommunicationQuorum();
        //getObjectCommunicationQuorum();
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

            // previously the object is hosted on every possible node of the trust domain.
            //now we change with the inferred result.

            /*H hosts = getHostByPartitionedClass(object);
            if(objectsH.containsKey(objField.getName()))
                objectsH.put(objField.getName(), H.union(objectsH.get(objField.getName()), hosts));
            else
                objectsH.put(objField.getName(), hosts);*/
        }
    }

/*    private void extractMethodHosts(PartitionedObject object, String configPath)
    {
        for(Method m : object.getClass().getDeclaredMethods()) {
            H allHosts = getHostByPartitionedClass(object);
            H requiredHosts = allHosts.pickFirst(2 * resiliencyConfiguration.getPrincipalResiliency(getPrincipalNameByPartitionedClass(object)) + 1);
            if(methodsH.containsKey(m.getName()))
                methodsH.put(m.getName(), H.union(methodsH.get(m.getName()), requiredHosts));
            else
                methodsH.put(m.getName(), requiredHosts);
        }
    }*/

/*    private void getMethodCommunicationQuorum()
    {
        methodsQ = new HashMap<>();
        switch (useCaseName)
        {
*//*            case "ot":
                methodsQ.put("m1", new P(configurations.get("Client").getHostSet(), 1));
                methodsQ.put("ret", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                break;*//*
            // old non-optimized ott
//            case "ott":
//                methodsQ.put("m4", new P(configurations.get("Client").getHostSet(), 1));
//                methodsQ.put("m3", new PAnd(new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1), new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1)));
//                methodsQ.put("m2", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
//                methodsQ.put("m1", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
//                methodsQ.put("ret", new POr(
//                        new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1),
//                        new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1)
//                    )
//                );
//                break;
            case "ott":
                methodsQ.put("m3", new P(configurations.get("Client").getHostSet(), 1));
                methodsQ.put("m2", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("m1", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("m0", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("ret", new POr(
                                new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1),
                                new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1)
                        )
                );
                break;
*//*            case "tc":
                methodsQ.put("m2", new P(configurations.get("Client").getHostSet(), 1));
                methodsQ.put("m3", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                methodsQ.put("m5", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                methodsQ.put("m6", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("m7", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                methodsQ.put("m8", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                methodsQ.put("ret", new POr(
                                new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1),
                                new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1)
                        )
                );
                break;
            case "ac":
                methodsQ.put("m1", new POr(new P(configurations.get("Client").getHostSet(), 1), new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1)));
                methodsQ.put("m2", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                methodsQ.put("m3", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                methodsQ.put("m4", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                methodsQ.put("m5", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                methodsQ.put("m6", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("ret", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                break;
            case "mpc":
                methodsQ.put("m1", new P(configurations.get("Client").getHostSet(), 1));
                methodsQ.put("m2", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                methodsQ.put("m3", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("ret", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                break;
            case "friendmap":
                methodsQ.put("m1", new P(configurations.get("Client").getHostSet(), 1));
//                methodsQ.put("m2", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                methodsQ.put("m6", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("m9", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("m3", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                methodsQ.put("m5", new P(configurations.get("S").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("S") + 1));
                methodsQ.put("m8", new P(configurations.get("M").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("M") + 1));
                methodsQ.put("m4", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                methodsQ.put("m7", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                methodsQ.put("ret", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                break;*//*
        }

    }

    private void getObjectCommunicationQuorum()
    {
        objectsQ = new HashMap<>();
        switch (useCaseName) {
*//*            case "ot":
                objectsQ.put("r1", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                objectsQ.put("r2", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                objectsQ.put("r", new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1));
                break;*//*
            //old non-optimized ott
//            case "ott":
//                objectsQ = new HashMap<>();
//                objectsQ.put("i1", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
//                objectsQ.put("i2", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
//                objectsQ.put("a", new POr(new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1), new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1)));
//                break;
            case "ott":
                objectsQ = new HashMap<>();
                objectsQ.put("i1", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                objectsQ.put("i2", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                objectsQ.put("a", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                break;
*//*            case "tc":
                objectsQ.put("airlineAgent", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                objectsQ.put("bankAgent", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                objectsQ.put("userAgent", new POr(
                                new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1),
                                new POr(
                                        new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1),
                                        new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1)
                                )
                        )
                );
                break;
            case "ac":
                objectsQ.put("agentA", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                objectsQ.put("agentB", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                objectsQ.put("userAgent", new POr(
                                new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1),
                                new POr(
                                        new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1),
                                        new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1)
                                )
                        )
                );
                break;
            case "mpc":
                objectsQ.put("a",
                        new POr(new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1),
                                new POr(new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1),
                                        new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1))));
                objectsQ.put("b",
                        new POr(new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1),
                                new POr(new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1),
                                        new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1))));
                objectsQ.put("c",
                        new POr(new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1),
                                new POr(new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1),
                                        new P(configurations.get("C").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("C") + 1))));
                break;
            case "friendmap":
                objectsQ.put("alice", new P(configurations.get("A").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("A") + 1));
                objectsQ.put("bob", new P(configurations.get("B").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("B") + 1));
                objectsQ.put("snapp", new P(configurations.get("S").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("S") + 1));
                objectsQ.put("mapService", new P(configurations.get("M").getHostSet(), resiliencyConfiguration.getPrincipalResiliency("M") + 1));
                break;*//*
        }
    }*/

    public void writeConfigsAndFinalize()
    {
        String configPath = "config" + "_" + resiliencyConfiguration;
        //String configPath = "config" + "_" + resiliencyStr;
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


        String runtimeConfigPath = "runtimeconfig" + "_"+ resiliencyConfiguration;
        //String runtimeConfigPath = "runtimeconfig" + "_"+ resiliencyStr;
        directory = new File(runtimeConfigPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        // create runtime configuration
        writeHostsConfigFile(getAllHosts(), 1, runtimeConfigPath, basePort);
        writeSystemConfigFile(getAllHosts(), 1, runtimeConfigPath);
    }

    private void readSpecificationFromFile(String configpath)
    {
        //HashMap<String,Configuration> configs = new HashMap<>();
        //record the trust domain order
        ArrayList<String> nameOrder = new ArrayList<>();
        HashMap<Integer, String> nameOrder2 = new HashMap<>();
        Boolean changeEntranceMethodQ = false;
        String entranceMethodName = new String();
        try
        {
            FileReader fr = new FileReader(configpath);
            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            //produce the configuration from n and principal set.
            //eg: n = 3, principals = [7, 4, 1] => A, B, Client
            //we need to distinguish client and other trust domains.
            //The result of the type inference may not include client trust domain. But it is default input for the run-time
            //todo: produce domain name. currently we use the name given in the file
            while ((line = rd.readLine()) != null) {
                if(line.startsWith("#")){
                    //String hostsSetName = line.split("\\s+")[0];
                    //String partitionedClassName = line.split("\\s+")[1];
                    //String hostList = line.split("\\s+")[2];
                    //Configuration c = new Configuration(hostsSetName, partitionedClassName, hostList);
                    String hostsSetName = line.split("\\s+")[1];
                    String partitionedClassName = line.split("\\s+")[2];
                    Configuration c = new Configuration(hostsSetName, partitionedClassName);
                    //configs.put(hostsSetName, c);
                    configurations.put(hostsSetName, c);
                    nameOrder2.put(nameOrder.size(), hostsSetName);
                    nameOrder.add(hostsSetName);
                }
                //define the number of trust domain
                else if(line.startsWith("n =")){
                    String num = line.replaceAll("[^\\d]", "");
                    n = Integer.valueOf(num);
                }
                //define the host list for configs
                else if(line.startsWith("principals")){
                    String p = line.replaceAll("[^\\d]", " ");
                    p = p.trim();
                    int base = 0;
                    for(int i = 0; i < n; i++){
                        int pivot = Integer.parseInt(p.split("\\s+")[i]);
                        int top = base + pivot;
                        //configs.get(nameOrder.get(i)).addHostSet(hostListFromNum(base, top));
                        configurations.get(nameOrder.get(i)).addHostSet(hostListFromNum(base, top));
                        base = top;
                    }
                    //if we did not define the client field, then we add it in the map manually.
                    if(n < configurations.keySet().size()){
                        ArrayList<Integer> clientH = new ArrayList<>();
                        clientH.add(base);
                        configurations.get("Client").addHostSet(clientH);
                    }
                }
                //regular expression for method hosts
                else if(Pattern.compile("resH|m\\d+H").matcher(line).find()){
                    String methodName = line.substring(0, line.indexOf('H'));
                    if(methodName.equals("res")){
                        methodName = "ret";
                    }
                    String str = line.substring(line.indexOf('['), line.indexOf(']')+1);
                    //produce the host from string like: [0, 0, 1]
                    str = str.replaceAll("[^\\d]", " ");
                    str =  str.trim();
                    H mH = new H();
                    int totalHostNum = 0;
                    for(int i = 0; i < n; i++){
                        int hNum = Integer.parseInt(str.split("\\s+")[i]);
                        if(hNum <= 0){ }
                        else {
                            //get host by the order
                            //mH = mH.union(mH, configs.get(nameOrder.get(i)).getHostSet().pickFirst(hNum));
                            totalHostNum += hNum;
                            mH = mH.union(mH, configurations.get(nameOrder.get(i)).getHostSet().pickFirst(hNum));
                            mH.setName(configurations.get(nameOrder.get(i)).getHostSet().getName());
                        }
                    }
                    //if the retH > 1, then we need to manually set retH on the one node where bftsmart.Client object is.
                    if(methodName.equals("ret")){
                        methodsH.put(methodName, configurations.get("Client").getHostSet());
                    }
                    else {
                        methodsH.put(methodName, mH);
                    }
                }
                //regular expression for method communication quorums
                else if(Pattern.compile("resQ|m\\d+Q").matcher(line).find()){
                    String methodName = line.substring(0, line.indexOf('Q'));
                    if(methodName.equals("res")){
                        methodName = "ret";
                    }
                    String str = line.substring(line.indexOf('['), line.indexOf(']')+1);
                    //produce the host from string like: [0, 0, 1]
                    methodsQ.put(methodName, quorumSTranslation(str, n, nameOrder2));
                    entranceMethodName = methodName;
                }
                //regular expression for object communication quorums
                else if(Pattern.compile("\\w+qc").matcher(line).find()){
                    //change the communication quorum for the entrance method to be Client host
                    if(!changeEntranceMethodQ){
                        changeEntranceMethodQ = true;
                        Q entranceQ = new P(methodsH.get("ret"), 1);
                        entranceQ = new POr(entranceQ, methodsQ.get(entranceMethodName));
                        methodsQ.put(entranceMethodName, entranceQ);
                    }
                    String objectName = line.substring(0, line.indexOf("qc"));
                    String str = line.substring(line.indexOf('['), line.indexOf(']')+1);
                    objectsQ.put(objectName, quorumSTranslation(str, n, nameOrder2));
                }
                //regular expression for object hosts
                else if(Pattern.compile("\\w+OH").matcher(line).find()){
                    String objectName = line.substring(0, line.indexOf("OH"));
                    String str = line.substring(line.indexOf('['), line.indexOf(']')+1);
                    //produce the host from string like: [0, 0, 1]
                    str = str.replaceAll("[^\\d]", " ");
                    str =  str.trim();
                    H oH = new H();
                    for(int i = 0; i < n; i++){
                        int hNum = Integer.parseInt(str.split("\\s+")[i]);
                        if(hNum <= 0){ }
                        else {
                            //get host by the order
                            //oH = oH.union(oH, configs.get(nameOrder.get(i)).getHostSet().pickFirst(hNum));
                            oH = oH.union(oH, configurations.get(nameOrder.get(i)).getHostSet().pickFirst(hNum));
                            oH.setName(configurations.get(nameOrder.get(i)).getHostSet().getName());
                        }
                    }
                    objectsH.put(objectName, oH);
                }
            }
            fr.close();
            rd.close();
        }
        catch (IOException e)
        {
            System.out.println("Cannot read use-case config file");
        }
        //return configs;
        return;
    }

    //produce host list give lower bound and upper bound [lb, ub)
    private ArrayList<Integer> hostListFromNum(int lowerBound, int upperBound){
        ArrayList result = new ArrayList();
        for(int i = lowerBound; i < upperBound; i++){
            result.add(i);
        }
        return result;
    }

    //translate a string representation of quorum system to a quorum system  Q in bft smart run-time
    //for example: [3, 0, 0, 0, 0, 0, 0, 2, 0] or [0, 2, 0, 0, 0, 0, 0, 0, 0]
    private Q quorumSTranslation(String quorumS, int n, HashMap<Integer, String> tMap){
        // Replacing every non-digit number with ""
        quorumS = quorumS.replaceAll("[^\\d]", "");
        int quorumNum = 0;
        ArrayList<String> quorumString = new ArrayList<>();
        ArrayList<Q> quorumSystems = new ArrayList<>();

        for(int i = 0; i < quorumS.length(); i = i+n){
            //if the quorum is empty, we can ignore it
            if(isEmptyQuorum(quorumS.substring(i, i+n))){}
            //if the quorum is not empty, we need to add it to P
            else {
                quorumNum++;
                quorumString.add(quorumS.substring(i, i+n));
            }
        }

        for(int j = 0; j < quorumNum; j++){
            quorumSystems.add(quorumTranslation(quorumString.get(j), tMap));
        }

        return recursionOr(quorumSystems);
    }

    //filter empty quorum
    private Boolean isEmptyQuorum(String q){
        if (q.matches("[0]+")){
            return true;
        }
        else return false;
    }

    //translate a quorum like 300 to P3(A) in bft smart
    private Q quorumTranslation(String quorum, HashMap<Integer, String> trustDomain){
        int andNum = 0;
        ArrayList<Integer> indexForDomain = new ArrayList<>();
        for(int i = 0; i < quorum.length(); i++){
            if(Character.getNumericValue(quorum.charAt(i)) > 0){
                andNum++;
                indexForDomain.add(i);
            }
        }
        return recursionAnd(quorum, trustDomain, andNum, indexForDomain);
    }

    //eg: quorum: 300, andNum: 1, indexForDomain:[0]
    //or quorum: 312, andNum: 3, indexForDomain:[0, 1, 2]
    private Q recursionAnd(String quorum, HashMap<Integer, String> trustDomain, int andNum, ArrayList<Integer> indexForDomain){
        if(andNum == 1){
            P result = new P(configurations.get(trustDomain.get(indexForDomain.get(0))).getHostSet(),
                    Character.getNumericValue(quorum.charAt(indexForDomain.get(0))));
            return  result;
        }
        else{
            P firstQ = new P(configurations.get(trustDomain.get(indexForDomain.get(0))).getHostSet(),
                    Character.getNumericValue(quorum.charAt(indexForDomain.get(0))));
            indexForDomain.remove(0);
            PAnd result = new PAnd(firstQ,
                    recursionAnd(quorum, trustDomain, andNum -1, indexForDomain));
            return result;
        }
    }

    //use quorum to construct quorum system
    private Q recursionOr(ArrayList<Q> quorum){
        if(quorum.size() == 1){
            return quorum.get(0);
        }
        else {
            Q firstQ = quorum.get(0);
            quorum.remove(0);
            POr result = new POr(firstQ, recursionOr(quorum));
            return result;
        }
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
            5) leader faults
            6) follower faults
            7) random faults
            8) faults type (0: crash, 1: byz message, 2: 5(s) delay)
             */
            String leaderFaults = "";
            String followerFaults = "";
            String randomFaults = "";
            String faultsType = "";
            int numInjectedFaults = (RMIRuntime.NUMBER_OF_FAULTS == Integer.MAX_VALUE) ? (h.size()-1)/3 : RMIRuntime.NUMBER_OF_FAULTS;
            boolean noFault = false;

            // condition that injects fault only in the cluster 2 (since cluster 1 and 2 both are placed on B)
            if(RMIRuntime.NUMBER_OF_FAULTS == Integer.MAX_VALUE && useCaseName.equals("ott")) {
                if (clusterID == 1)
                    noFault = true;
            }

            if(RMIRuntime.NUMBER_OF_FAULTS != Integer.MAX_VALUE) {
                switch (useCaseName) {
                    case "ott":
                        // condition that injects fault only in cluster2 (since cluster 1 and 2 both are placed on B)
                        if (!h.getName().equals("B") || clusterID != 2)
                            noFault = true;
                        break;
                    case "friendmap":
                        if (!h.getName().equals("A"))
                            noFault = true;
                        break;
                    case "tc":
                        if (h.getName().equals("C"))
                            noFault = true;
                        break;
                    case "mpc":
                        if (!h.getName().equals("A"))
                            noFault = true;
                        break;
                    case "ot":
                        noFault = true;
                        break;
                    case "ac":
                        noFault = true;
                        break;
                }
            }
            if(!noFault) {
                for (int i = 0; i < numInjectedFaults; i++) {
                    if (i != numInjectedFaults - 1) {
                        leaderFaults += h.toIntArray()[i] + ",";
                        followerFaults += h.toIntArray()[h.size() - 1 - i] + ",";
                        int f = 0;
                        if (h.size() == 1)
                            f = h.toIntArray()[0];
                        else {
                            f = h.toIntArray()[new Random().nextInt(h.size())];
                            while (randomFaults.contains(String.valueOf(f)))
                                f = h.toIntArray()[new Random().nextInt(h.size())];
                        }
                        randomFaults += f + ",";

                        faultsType += new Random().nextInt(3) + ",";
                    } else {
                        leaderFaults += h.toIntArray()[i];
                        followerFaults += h.toIntArray()[h.size() - 1 - i];
                        int f = 0;
                        if (h.size() == 1)
                            f = h.toIntArray()[0];
                        else {
                            f = h.toIntArray()[new Random().nextInt(h.size())];
                            while (randomFaults.contains(String.valueOf(f)))
                                f = h.toIntArray()[new Random().nextInt(h.size())];
                        }
                        randomFaults += f;

                        faultsType += new Random().nextInt(3);
                    }
                }
            }
            file = String.format(file, clusterID, h.size(), (h.size()-1)/3, initView, leaderFaults, followerFaults, randomFaults, faultsType);
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
        try {
            String sep = System.getProperty("file.separator");
            String fileName = configPath + sep + "hosts.config" + clusterID;
            if(!new File(fileName).exists()) {
                PrintWriter aWriter = new PrintWriter(configPath + sep + "hosts.config" + clusterID, "UTF-8");
                for (int i = 0; i < h.size(); i++) {
//                    String hostLine = i + baseHostID + " " + hostsIpMap.get(i + baseHostID) + " " + String.valueOf(basePort + (10 * i)) + " " + String.valueOf(basePort + 1 + (10 * i)) + " " + String.valueOf(basePort + 2 + (10 * i)) + "\n";
                    String hostLine = h.toIntArray()[i] + " " + hostsIpMap.get(h.toIntArray()[i]) + " " + String.valueOf(basePort + (10 * i)) + " " + String.valueOf(basePort + 1 + (10 * i)) + " " + String.valueOf(basePort + 2 + (10 * i)) + "\n";
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

    public ResiliencyConfiguration getResiliencyConfiguration() {
        return resiliencyConfiguration;
    }

    public H getAllHosts()
    {
        H hs = new H();
        for (Map.Entry<String,Configuration> config : configurations.entrySet())
            hs = H.union(hs, config.getValue().getHostSet());
        hs.setName("all");
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


