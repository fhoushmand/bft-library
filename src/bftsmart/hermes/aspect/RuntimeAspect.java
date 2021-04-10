/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bftsmart.hermes.aspect;

/**
 *
 * @author rmartins
 */

import bftsmart.hermes.runtime.HermesRuntime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class RuntimeAspect {

//    @Around("execution (* bftsmart.usecase.NodeClusterRunner.main*(..))")
    public void advice(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around NodeClusterRunner main function");
        String[] args = (String[]) joinPoint.getArgs()[0];
        String id = args[1];
        System.out.printf("RuntimeAspect.advice() called on '%s'\n", joinPoint);
        HermesRuntime.getInstance().setID(id);
//        String logName = HermesConfig.getWorkingDir() + "node_" + prefix+"_"+
//        		HermesRuntime.getInstance().getRuntimeID() + ".log";
//        HermesConfig.addLoggerFile(logName);
        try {
            HermesRuntime.getInstance().open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        joinPoint.proceed();
    }
}