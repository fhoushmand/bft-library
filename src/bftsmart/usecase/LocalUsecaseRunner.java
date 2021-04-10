package bftsmart.usecase;

import bftsmart.demo.airlineagent.AirlineAgentClient;
import bftsmart.demo.airlineagent.AirlineAgentServer;
import bftsmart.demo.register.BooleanRegisterClient;
import bftsmart.demo.register.BooleanRegisterServer;
import bftsmart.demo.register.IntegerRegisterClient;
import bftsmart.demo.register.IntegerRegisterServer;
import bftsmart.runtime.CMDReader;
import bftsmart.runtime.RMIRuntime;

import java.util.Map;

/*
* args[0] config path
* args[1] faults number
 */
public class LocalUsecaseRunner {
    public static void main(String[] args) throws Exception {
        RMIRuntime.CONFIGURATION = args[0].split("/")[args[0].split("/").length - 1];
        if(args[1].equals("max"))
            RMIRuntime.NUMBER_OF_FAULTS = Integer.MAX_VALUE;
        else
            RMIRuntime.NUMBER_OF_FAULTS = Integer.valueOf(args[1]);
        NodeClusterRunner.local = true;
        Spec spec = new Spec(true, args[0], null);
        for (Configuration config : spec.getConfigurations().values()) {
            if (config.getPrincipalName().equals("Client"))
                Thread.sleep(20000);
            for (Integer host : config.getHostSet().toIntArray()) {
                new Thread(() -> {
                    try {
                        NodeClusterRunner.main(new String[]{args[0], String.valueOf(host), null});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}
