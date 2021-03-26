/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package bftsmart.runtime;

import bftsmart.communication.SystemMessage;
import bftsmart.reconfiguration.ServerViewController;

import bftsmart.usecase.Spec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class ServerCommunicationSystem extends Thread {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean doWork = true;
    public final long MESSAGE_WAIT_TIME = 100;
    private LinkedBlockingQueue<RTMessage> inQueue = null;
    protected MessageHandlerRMI messageHandlerRMI;
    
    private ServersCommunicationLayer serversConn;

    /**
     * Creates a new instance of ServerCommunicationSystem
     */
    public ServerCommunicationSystem(ServerViewController controller, MessageHandlerRMI handlerRMI, Spec spec) throws Exception {
        super("Server Comm. System");
        
        messageHandlerRMI = handlerRMI;

        inQueue = new LinkedBlockingQueue<RTMessage>(100);

        serversConn = new ServersCommunicationLayer(controller, inQueue, spec);
    }

    /**
     * Thread method responsible for receiving messages sent by other servers.
     */
    @Override
    public void run() {
        
        long count = 0;
        RTMessage sm = null;
        while (doWork) {
            try {
                if (count % 1000 == 0 && count > 0) {
                    logger.debug("After " + count + " messages, inQueue size=" + inQueue.size());
                }

                sm = inQueue.poll(MESSAGE_WAIT_TIME, TimeUnit.MILLISECONDS);

                if (sm != null) {
                    logger.debug("<-- receiving, msg:" + sm + " from " + sm.getSender());
                    messageHandlerRMI.processData(sm);
                    count++;
                }
            } catch (Exception e) {
                
                logger.error("Error processing message",e);
            }
        }
        logger.info("ServerCommunicationSystem stopped.");

    }

    /**
     * Send a message to target processes. If the message is an instance of 
     * RTMessage, it is sent to the clients, otherwise it is set to the
     * servers.
     *
     * @param targets the target receivers of the message
     * @param sm the message to be sent
     */
    public void send(int[] targets, RTMessage sm) {
        if(sm instanceof MethodCallMessage)
            logger.debug("sending message {} from: {} -> {}", sm.getOperationId(), sm.getSender(), targets);
        else if(sm instanceof ObjCallMessage)
            logger.debug("sending message {} from: {} -> {}", sm.getOperationId(), sm.getSender(), targets);
        serversConn.send(targets, sm);
    }
    
    @Override
    public String toString() {
        return serversConn.toString();
    }
    
    public void shutdown() {
        
        logger.info("Shutting down communication layer");
        
        this.doWork = false;
        serversConn.shutdown();
    }
}
