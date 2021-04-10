package bftsmart.usecase;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.demo.airlineagent.AirlineAgentServer;
import bftsmart.demo.bankagent.BankAgentClient;
import bftsmart.demo.bankagent.BankAgentServer;
import bftsmart.demo.friendmap.*;
import bftsmart.demo.mpc.MPCClient;
import bftsmart.demo.mpc.MPCServer;
import bftsmart.demo.register.*;
import bftsmart.demo.useragent.UserAgentClient;
import bftsmart.demo.useragent.UserAgentServer;
import bftsmart.runtime.CMDReader;
import bftsmart.runtime.RMIRuntime;

import java.util.Arrays;
import java.util.Map;

public class NodeClusterRunner {
    /*
     * @param args[0] is the config file in systemconfig
     * @param args[1] is the id of the current runtime
     * @param args[2:n] is the host list name if running on the cluster otherwise it can be null
     */
    public static boolean local = true;
    public static void main(String[] args) throws Exception {
        Spec spec = null;
        if(local)
            spec = new Spec(true, args[0], null);
        else
            spec = new Spec(false, args[0], Arrays.copyOfRange(args, 2, args.length));
        RMIRuntime.CONFIGURATION = args[0].split("/")[args[0].split("/").length - 1];
        RMIRuntime.USECASE_NAME = args[0].split("/")[args[0].split("/").length - 2];
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
                    else if (object.getValue().getKey().equals(BankAgentClient.class)) {
                        new BankAgentServer(0, host, spec.getClusterIDByObjectField(object.getKey()));
                    }
                    else if (object.getValue().getKey().equals(UserAgentClient.class)) {
                        new UserAgentServer(0, host, spec.getClusterIDByObjectField(object.getKey()));
                    }
                    else if (object.getValue().getKey().equals(MPCClient.class)) {
                        int init = 500;
//                        if(host <= 9)
//                            init = 200;
//                        else if(host > 9 && host <= 19)
//                            init = 400;
//                        else if(host > 19 && host <= 29)
//                            init = 600;
//                        if(host <= 3)
//                            init = 200;
//                        else if(host > 3 && host <= 7)
//                            init = 400;
//                        else if(host > 7 && host <= 11)
//                            init = 600;
                        new MPCServer(init, host, spec.getClusterIDByObjectField(object.getKey()));
                    }
                    else if(object.getValue().getKey().equals(AliceClient.class))
                    {
                        new AliceServer(0, host, spec.getClusterIDByObjectField(object.getKey()));
                    }
                    else if(object.getValue().getKey().equals(BobClient.class))
                    {
                        new BobServer(0, host, spec.getClusterIDByObjectField(object.getKey()));
                    }
                    else if(object.getValue().getKey().equals(SnappClient.class))
                    {
                        System.out.println("starting alice server");
                        new SnappServer(0, host, spec.getClusterIDByObjectField(object.getKey()));
                    }
                    else if(object.getValue().getKey().equals(MapServiceClient.class))
                    {
                        new MapServiceServer(0, host, spec.getClusterIDByObjectField(object.getKey()));
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

