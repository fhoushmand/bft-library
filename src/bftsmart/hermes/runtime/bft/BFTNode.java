/*
 *  Copyright 2012 Carnegie Mellon University  
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */
package bftsmart.hermes.runtime.bft;

import bftsmart.hermes.HermesConfig;
import bftsmart.hermes.orchestration.actions.Action;
import bftsmart.hermes.orchestration.actions.SpeedBumpAction;
import bftsmart.hermes.orchestration.actions.SpeedBumpActionResult;
import bftsmart.hermes.orchestration.notification.Notification;
import bftsmart.hermes.orchestration.notification.SetStateNotification;
import bftsmart.hermes.runtime.HermesFault;
import bftsmart.hermes.runtime.HermesRuntime;
import bftsmart.hermes.runtime.faultinjection.CPULoaderFaultAlgorithm;
import bftsmart.hermes.runtime.faultinjection.CrashFault;
import bftsmart.hermes.runtime.faultinjection.NetworkCorrupterAlgorithm;
import bftsmart.hermes.runtime.faultinjection.ThreadDelayFault;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rolando Martins <rolando.martins@gmail.com>
 */
public class BFTNode {

//    static {
//        try {
//            HermesRuntime.hermesRuntime.open();
//        } catch (Exception ex) {
//            Logger.getLogger(P3Node.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    String m_cellID = null;
    HermesRuntime m_runtime = new HermesRuntime();

    public BFTNode() {
    }

    public void open(String id) throws Exception {
        m_runtime.setID(id);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date();
        dateFormat.format(date);
        String logName = HermesConfig.getWorkingDir()+"/node_"+m_runtime.getRuntimeID() + "_" + dateFormat.format(date) + ".log";
        
        HermesConfig.addLoggerFile(logName);
        m_runtime.open();
    }

    public HermesRuntime getRuntime() {
        return m_runtime;
    }

  
    public void onSetState() {
        Notification notification = new SetStateNotification(HermesRuntime.getRandomUUID(),
                "djhskjsaasjkdh", "a0483ab869c2ef065e3a1153c8831882", 1);
        m_runtime.notification(notification, 2500);
    }

    public void checkSpeedBump() throws Exception {
        Action action = new SpeedBumpAction(HermesRuntime.getRandomUUID());
        SpeedBumpActionResult result = (SpeedBumpActionResult) m_runtime.action(action, 2500);
        int speed = result.getSpeedBump();
        if (speed > 0) {
            System.out.println("\nSpeedBump " + speed + " !");
            Thread.sleep(speed);
        }
    }

    public static void main(String[] args) {
        final BFTNode node = new BFTNode();
        try {
            
            String id = null;
             System.out.println(args.length);
            if(args.length==0){
                id = HermesRuntime.getRandomUUID();
                Logger.getLogger(BFTNode.class.getName()).log(Level.INFO, "BFTNode with random id");
            }else{
                id = args[0];
                Logger.getLogger(BFTNode.class.getName()).log(Level.INFO, "BFTNode with assinged id");
            }
            node.open(id);
            Logger.getLogger(BFTNode.class.getName()).log(Level.INFO, "starting BFTNode id={0}", id);
/*             Action action = new ActionImpl(HermesRuntime.getRandomUUID(), "start_run");
             try {
             ActionResultImpl absresult = (ActionResultImpl) node.getRuntime().action(action, 2500);
             BooleanWrapperActionResult result = BooleanWrapperActionResult.allocate(absresult.getResult());
             System.out.println("ActionResult=" + result.getValue());
             } catch (Exception ex) {
             Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, null, ex);
             }*/
        } catch (Exception ex) {            
            Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        final String faultID = "2B4FA20ED54E4DA9B6B2A917D1FA723F";
       
        while (true) {
            try {
                HermesFault fault = node.getRuntime().getFaultManager().getFault(faultID);               
                //Logger.getLogger(BFTNode.class.getName()).log(Level.INFO, "\n\nFAILT={0}", fault);
                if (fault != null && fault.isEnabled()) {
                    //Logger.getLogger(BFTNode.class.getName()).log(Level.INFO, "\n\nFAILT={0}", fault);
                    //System.out.println("\n\nFAILT="+fault);
                    switch (fault.getSerialID()) {
                        case CPULoaderFaultAlgorithm.SERIAL_ID:
                        case CrashFault.SERIAL_ID:
                        case ThreadDelayFault.SERIAL_ID:
                            try {
                                 Logger.getLogger(BFTNode.class.getName()).log(Level.INFO, "Executing FAULT={0}", fault);
                                fault.execute();
                                fault.disable();
                                Logger.getLogger(BFTNode.class.getName()).log(Level.INFO, "After FAULT={0}", fault);
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
                                fault.execute();
                            } catch (Exception ex) {
                                Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(BFTNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        try {
//            m_runtime.close();
//        } catch (Exception ex) {
//            Logger.getLogger(P3Node.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void close() {
        m_runtime.close();
    }
}
