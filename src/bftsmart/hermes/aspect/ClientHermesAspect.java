package bftsmart.hermes.aspect;

/**
 *
 * @author rmartins
 */

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.JoinPoint;

import bftsmart.hermes.HermesConfig;
import bftsmart.hermes.runtime.HermesRuntime;

@Aspect
public class ClientHermesAspect {

//    @Before("execution (* bftsmart.demo.counter.CounterClient.main*(..))")
    public void advice(JoinPoint joinPoint) {
        System.out.println("teststststst");
        String[] args = (String[]) joinPoint.getArgs()[0];
        String id = args[0];
        System.out.printf("StartHermesAspect.advice() called on '%s'%n", joinPoint);
        //HermesRuntime m_runtime = new HermesRuntime();
        HermesRuntime.getInstance().setID(id);
        String prefix = "replica";
        if(id.compareTo("1001")==0){
            System.out.println("\nCLIENT\n");
            prefix = "client";
        }else{
            System.out.println("\nREPLICA\n");
        }
        String logName = HermesConfig.getWorkingDir() + "node_" + prefix+"_"+
                HermesRuntime.getInstance().getRuntimeID() + ".log";
        HermesConfig.addLoggerFile(logName);
        try {
            HermesRuntime.getInstance().open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}