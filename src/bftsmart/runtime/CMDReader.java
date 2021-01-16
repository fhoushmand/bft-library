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
import java.util.Random;

public class CMDReader extends Thread {

    public RMIRuntime runtime;

    public CMDReader() { }

    @Override
    public void run() {
        try {
            Thread.sleep(100);
            for (int i = 0; i < 1000; i++) {
                ((Client) runtime.obj).request(300 + Integer.valueOf(new Random().nextInt(50)));
                runtime.obj.objCallLock.lock();
                runtime.obj.requestBlock.await();
                runtime.obj.objCallLock.unlock();
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
