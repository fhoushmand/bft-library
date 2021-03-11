package bftsmart.usecase;

import bftsmart.demo.register.BooleanRegisterClient;
import bftsmart.demo.register.BooleanRegisterServer;
import bftsmart.demo.register.IntegerRegisterClient;
import bftsmart.demo.register.IntegerRegisterServer;
import bftsmart.reconfiguration.util.SunECKeyLoader;
import bftsmart.runtime.CMDReader;
import bftsmart.runtime.RMIRuntime;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;

import java.util.Arrays;
import java.util.Map;

public class LocalObjectsReplicationService {
    /*
     * @param args[0] is the config file in systemconfig
     */
    public static boolean local = true;
    public static void main(String[] args) throws Exception {
        Spec spec = new Spec(true, args[0], null);
        RMIRuntime.CONFIGURATION = args[0].split("/")[args[0].split("/").length - 1];
        for (Map.Entry<String, Map.Entry<Class, Integer>> object : spec.getObjectFields().entrySet()) {
            for (int objectHost : spec.getObjectsH().get(object.getKey()).toIntArray()) {
                if (object.getValue().getKey().equals(BooleanRegisterClient.class)) {
                    Boolean initValue = Boolean.FALSE;
                    Spec finalSpec = spec;
                    new Thread(() -> {
                        new BooleanRegisterServer(initValue, objectHost, finalSpec.getClusterIDByObjectField(object.getKey()));
                    }).start();
                } else if (object.getValue().getKey().equals(IntegerRegisterClient.class)) {
                    Integer initValue = 10;
                    Spec finalSpec1 = spec;
                    new Thread(() -> {
                        new IntegerRegisterServer(initValue, objectHost, finalSpec1.getClusterIDByObjectField(object.getKey()));
                    }).start();
                }
            }
        }
    }
}

