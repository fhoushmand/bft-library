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

import bftsmart.usecase.Client;
import bftsmart.usecase.auction.AuctionClient;
import bftsmart.usecase.friendmap.FriendMapClient;
import bftsmart.usecase.mpc.MPCClient;
import bftsmart.usecase.obltransfer.OblTransferClient;
import bftsmart.usecase.onetimetransfer.OTTClient;
import bftsmart.usecase.ticket.TicketSystemClient;

import java.util.Random;

public class CMDReader extends Thread {

    public static int TRANSFER_USECASES_REP = 150;
    public static int MPC_USECASES_REP = 1500;
    public static int FRIENDMAP_USECASES_REP = 150;
    public static int TICKET_USECASE_REP = 150;
    public static int AUCTION_USECASE_REP = 150;


    public RMIRuntime runtime;

    public CMDReader() { }

    @Override
    public void run() {
        try {
            if(runtime.obj instanceof AuctionClient) {
                for (int i = 0; i < AUCTION_USECASE_REP; i++) {
                    ((Client) runtime.obj).request(301 + new Random().nextInt(100));
                    runtime.obj.objCallLock.lock();
                    runtime.obj.requestBlock.await();
                    runtime.obj.objCallLock.unlock();
                }
            }
            else if(runtime.obj instanceof OTTClient || runtime.obj instanceof OblTransferClient)
            {
                for (int i = 0; i < TRANSFER_USECASES_REP; i++) {
                    ((Client) runtime.obj).request(new Random().nextInt(2));
                    runtime.obj.objCallLock.lock();
                    runtime.obj.requestBlock.await();
                    runtime.obj.objCallLock.unlock();
                }
            }
            else if(runtime.obj instanceof TicketSystemClient)
            {
                for (int i = 0; i < TICKET_USECASE_REP; i++) {
                    ((Client) runtime.obj).request(2);
                    runtime.obj.objCallLock.lock();
                    runtime.obj.requestBlock.await();
                    runtime.obj.objCallLock.unlock();
                }
            }
            else if(runtime.obj instanceof MPCClient)
            {
                for (int i = 0; i < MPC_USECASES_REP; i++) {
                    ((Client) runtime.obj).request();
                    runtime.obj.objCallLock.lock();
                    runtime.obj.requestBlock.await();
                    runtime.obj.objCallLock.unlock();
                }
            }
            else if(runtime.obj instanceof FriendMapClient)
            {
                for (int i = 0; i < FRIENDMAP_USECASES_REP; i++) {
                    ((Client) runtime.obj).request();
                    runtime.obj.objCallLock.lock();
                    runtime.obj.requestBlock.await();
                    runtime.obj.objCallLock.unlock();
                }
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
//        inQueue.offer("exit");
//        Scanner reader = new Scanner(System.in);
//        while (reader.hasNext()) {
//            String input = reader.nextLine();
//            inQueue.offer(input);
//        }
    }
}
