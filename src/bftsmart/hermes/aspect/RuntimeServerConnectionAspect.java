/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bftsmart.hermes.aspect;

/**
 *
 * @author Farzin
 */

import bftsmart.communication.server.ServerConnection;
import bftsmart.runtime.RTMessage;
import bftsmart.hermes.runtime.HermesFault;
import bftsmart.hermes.runtime.HermesRuntime;
import bftsmart.hermes.runtime.bft.BFTNode;
import bftsmart.hermes.runtime.faultinjection.*;
import bftsmart.hermes.runtime.faultinjection.bft.BFTDelayPacketFault;
import bftsmart.hermes.runtime.faultinjection.bft.BFTDelayPacketFaultDescription;
import bftsmart.hermes.runtime.faultinjection.bft.BFTForgePayloadFault;
import bftsmart.hermes.serialization.HermesSerializableHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bftsmart.hermes.runtime.faultinjection.bft.BFTForgePayloadFault.*;

@Aspect
public class RuntimeServerConnectionAspect {
    static Integer run = null;

//    @Around("execution (* bftsmart.runtime.ServerConnection.sendBytes(..))")
    public void advice(ProceedingJoinPoint joinPoint) throws Throwable {
        String faultID = "5B4FA20ED54E4DA9B6B2A917D1FA724F";
        HermesFault fault = HermesRuntime.getInstance().getFaultManager().getFault(faultID);
        if (fault != null) {

            if (fault.isEnabled()) {
                Integer a = null;
                byte[] messageData = (byte[]) joinPoint.getArgs()[0];
                try {
                    RTMessage sm = (RTMessage) (new ObjectInputStream(new ByteArrayInputStream(messageData)).readObject());
                    run = sm.getN();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

//                Integer run = (Integer) HermesRuntime.getInstance().getContext().getObject("RUN");
                if (run == null || run < 100) {
                    joinPoint.proceed();
                    return;
                }
                Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, "RUN: "+run);
                HermesRuntime.getInstance().getContext().put("RUN", run);
                String m = fault.getFaultContext().getString("f");
                if (!(m == null || m.compareToIgnoreCase("") == 0)) {
                    HermesRuntime.getInstance().getContext().put("f",
                            HermesSerializableHelper.stringToStringS(m));
                }
                try {

                    switch (fault.getSerialID()) {
                        case BFTForgePayloadFault.SERIAL_ID: {
                            BFTForgePayloadFault faultImpl = (BFTForgePayloadFault) fault;
                            HermesRuntime.getInstance().getContext().put("f",
                                    HermesSerializableHelper.stringToStringS(
                                    faultImpl.getFaultContext().getString("f")));
                            int type = faultImpl.getType();
                            long beforeFaultTime = System.currentTimeMillis();
                            Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, "Executing FAULT={0} Run{1}", new Object[]{fault,run});
                            
                            faultImpl.execute();
                            //System.out.println("BFTForgePayloadFault " + a + " " + paxostype);
                            switch (type) {
                                case MAXINT: {
                                    a = new Integer(4);
                                    break;
                                }
                                case NEGATIVE: {
                                    a = new Integer(5);
                                    break;
                                }
                                case CORRUPT_6: {
                                    a = new Integer(6);
                                    break;
                                }
                                case CORRUPT_7: {
                                    //Prepare messages, not weak
//                                    if (paxosMessage && paxostype == 44781) {
//                                        //if (paxostype != null && paxostype == 44782) {
//                                        long afterFaultTime = System.currentTimeMillis();
//                                        long delta = afterFaultTime - beforeFaultTime;
//                                        long sleepTime = FailureDetector.getSleepBellowTimeout() - delta;
//                                        if (sleepTime < 0) {
//                                            sleepTime = 0;
//                                            System.out.println("SET 7: Warning timeout are too small");
//                                        }
//                                        System.out.println("SET 7: delta:" + delta + " sleeping for " + sleepTime);
//                                        Thread.sleep(sleepTime);
//
//                                        a = new Integer(7);
//                                    }
                                    break;
                                }
                                case CORRUPT_8: {
//                                    if (paxosMessage && paxostype == 44781) {
//                                        //if (paxostype != null && paxostype == 44782) {
//                                        System.out.println("SET 8: sleeping for " + FailureDetector.getSleepAboveTimeout());
//                                        Thread.sleep(FailureDetector.getSleepAboveTimeout());
//                                        a = new Integer(8);
//                                    }
                                    break;
                                }
                                case CORRUPT_9: {
                                    a = new Integer(9);
                                    break;
                                }
                                case CORRUPT_10: {
                                    a = new Integer(10);
                                    break;
                                }
                            }
                            //System.out.println("-----BFTForgePayloadFault " + a + " " + paxostype + " run=" + run);
                            if (a != null && a != 7 && a != 8) {

                                ServerConnection obj = (ServerConnection) joinPoint.getTarget();
                                //System.out.println("111******BFTForgePayloadFault " + a + " " + paxostype + " run=" + run);
                                obj.aspectSendBytes(a, messageData);
                                //System.out.println("22****** AFTER  ****BFTForgePayloadFault " + a + " " + paxostype + " run=" + run);
                            } else {
                                break;
                            }
                            return;
                            //break;
                        }

                        case CPULoaderFaultAlgorithm.SERIAL_ID:
                        case CrashFault.SERIAL_ID:
                        case ThreadDelayFault.SERIAL_ID:
                            try {                                
                                try {
                                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Executing FAULT={0} Run{1}", new Object[]{fault,run});
                                    System.out.println("before execute");
                                    fault.execute();
                                    System.out.println("before disable");
                                    fault.disable();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    //return;
                                    break;
                                }
                                //Logger.getLogger(BFTNode.class.getName()).log(Level.INFO, "After FAULT={0}", fault);
                            } catch (Exception ex) {
                                Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        case NetworkCorrupterAlgorithm.SERIAL_ID: {
                            NetworkCorrupterAlgorithm faultImpl = (NetworkCorrupterAlgorithm) fault;
                            byte[] packet = new byte[10];
                            try {
                                //TODO
                                //faultImpl.setExecutionArgument(packet);
                                Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, "Executing FAULT={0} Run{1}", new Object[]{fault,run});
                                fault.execute();
                            } catch (Exception ex) {
                                Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, "injection not ready, but ok");
                                //Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                        case BFTDelayPacketFault.SERIAL_ID: {
                            BFTDelayPacketFault faultImpl = (BFTDelayPacketFault) fault;
                            int type = faultImpl.getDescription().getType();
                            //System.out.println("BFTDelayPacketFaultDescription TYPE:" + type);
                            switch (type) {
                                case BFTDelayPacketFaultDescription.ALL: {
                                    int id = Integer.parseInt(HermesRuntime.getInstance().getRuntimeID());
                                    //System.out.println("BFTDelayPacketFaultDescription.ALL: ID:" + id + " ROUND" + pm.getNumber());
                                    Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, "Executing FAULT={0} Run{1}", new Object[]{fault,run});
                                    faultImpl.execute();
                                    break;
                                }
                                case BFTDelayPacketFaultDescription.MOD_SEQ: {
                                    //int id = Integer.parseInt(HermesRuntime.getInstance().getRuntimeID());
                                    //int size = config.getF();
                                    
                                    String[] malicious = HermesSerializableHelper.stringToStringS(
                                    faultImpl.getFaultContext().getString("f"));
                                    //if ((pm.getNumber() % size) == id) {
//                                    for(int i=0; i < malicious.length;i++){
//                                        System.out.println(malicious[i]);
//                                    }
                                    int index = (run % malicious.length);
                                    //System.out.println("index="+index);
                                    //Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, "MOD malicious[(run % malicious.length)]={0} Run{1} index={1}", new Object[]{malicious[(run % malicious.length)],run,index});
                                    if (malicious[index].compareTo(HermesRuntime.getInstance().getRuntimeID())==0) {
                                        //System.out.println("BFTDelayPacketFaultDescription.MOD_SEQ: ID:" + id + " ROUND" + pm.getNumber() + " MOD" + (pm.getNumber() % size));
                                        Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, "Executing FAULT={0} Run{1}", new Object[]{fault,run});
                                        faultImpl.execute();
                                    }
                                    break;
                                }
                                default: {
                                    System.out.println("BFTDelayPacketFaultDescription. ERROR");
                                    break;
                                }
                            }
                            break;
                        }
                        case NetworkDropperFault.SERIAL_ID: {
                            NetworkDropperFault faultImpl = (NetworkDropperFault) fault;
                            Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, "Executing FAULT={0} Run{1}", new Object[]{fault,run});
                            faultImpl.execute();
                            if (faultImpl.filterPacket()) {
                                System.out.println("NetworkDropperFault:Fileting packet: " + HermesRuntime.getInstance().getRuntimeID());
                                return;
                            }
                        }
                    }
                } catch (Exception ex) {
                    if (ex.getLocalizedMessage().compareTo("Synchronization failed") != 0) {
                        System.out.println("ASPECT ECEPTION+++++++++++++++++++++++++++++++++++++:" + ex.getLocalizedMessage());
                        ex.printStackTrace();
                    }
                }
            }

        }
        joinPoint.proceed();
    }
}