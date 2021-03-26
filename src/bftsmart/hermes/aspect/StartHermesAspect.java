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
import org.aspectj.lang.annotation.*;

@Aspect
public class StartHermesAspect {

//    @Around("execution (* bftsmart.demo.counter.CounterServer.main*(..))")
    public void advice(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("teststststst");
        String[] args = (String[]) joinPoint.getArgs()[0];
        String id = args[0];
        System.out.printf("StartHermesAspect.advice() called on '%s'%n", joinPoint);
        HermesRuntime.getInstance().setID(id);
        try {
            HermesRuntime.getInstance().open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        joinPoint.proceed();
    }
}