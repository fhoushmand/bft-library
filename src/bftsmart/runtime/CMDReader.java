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


import bftsmart.usecase.oblivioustransfer.OTClient;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;


public class CMDReader extends Thread {

    private LinkedBlockingQueue<String> inQueue = null;

    /**
     * Creates a new instance of ServerCommunicationSystem
     */
    public CMDReader(LinkedBlockingQueue<String> inQueue) {
        super("Reader");

        this.inQueue = inQueue;
    }

    public LinkedBlockingQueue<String> getInQueue() {
        return inQueue;
    }

    public void setInQueue(LinkedBlockingQueue<String> inQueue) {
        this.inQueue = inQueue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            try {
                inQueue.offer(String.valueOf(new Random().nextInt(2)));
                Thread.sleep(2000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        inQueue.offer("exit");
//        Scanner reader = new Scanner(System.in);
//        while (reader.hasNext()) {
//            String input = reader.nextLine();
//            inQueue.offer(input);
//        }
    }
}
