package bftsmart.usecase;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.demo.airlineagent.AirlineAgentServer;
import bftsmart.demo.register.BooleanRegisterClient;
import bftsmart.demo.register.BooleanRegisterServer;
import bftsmart.demo.register.IntegerRegisterClient;
import bftsmart.demo.register.IntegerRegisterServer;
import bftsmart.runtime.CMDReader;
import bftsmart.runtime.RMIRuntime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NodeClusterRunner {
    /*
     * @param args[0] is the config file in systemconfig
     * @param args[1] is the id of the current runtime
     * @param args[2:n] is the host list name
     */
    public static boolean local = true;
    public static void main(String[] args) throws Exception {
        Spec spec = null;
        if(local)
            spec = new Spec(true, args[0], null);
        else
            spec = new Spec(false, args[0], Arrays.copyOfRange(args, 2, args.length));
        RMIRuntime.CONFIGURATION = args[0].split("/")[args[0].split("/").length - 1];
        int host = Integer.parseInt(args[1]);
        PartitionedObject o = (PartitionedObject) Class.forName(spec.getPartitionedClassByHostID(host)).getConstructor().newInstance();

        for (Map.Entry<String, Map.Entry<Class, Integer>> object : spec.getObjectFields().entrySet()) {
            for (int objectHost : spec.getObjectsH().get(object.getKey()).toIntArray()) {
                if (objectHost == host) {
                    if (object.getValue().getKey().equals(BooleanRegisterClient.class)) {
                        Boolean initValue = Boolean.FALSE;
                        new BooleanRegisterServer(initValue, host, spec.getClusterIDByObjectField(object.getKey()));
                    } else if (object.getValue().getKey().equals(IntegerRegisterClient.class)) {
                        Integer initValue = 10;
                        new IntegerRegisterServer(initValue, host, spec.getClusterIDByObjectField(object.getKey()));
                    }
                    else if (object.getValue().getKey().equals(AirlineAgentClient.class)) {
                        new AirlineAgentServer(0, host, spec.getClusterIDByObjectField(object.getKey()));
                    }
                }
            }
        }

            RMIRuntime runtime = new RMIRuntime(host, spec, o);
            runtime.start();

            //read from the queue (randomly generating inputs)
            if (runtime.getObj() instanceof Client) {
                CMDReader reader = new CMDReader();
                runtime.setInputReader(reader);
                reader.runtime = runtime;

            }
    }
}

