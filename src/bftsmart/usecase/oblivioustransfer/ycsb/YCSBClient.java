///**
// * Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package bftsmart.usecase.oblivioustransfer.ycsb;
//
//import bftsmart.runtime.CMDReader;
//import bftsmart.runtime.RMIRuntime;
//import bftsmart.usecase.PartitionedObject;
//import bftsmart.usecase.oblivioustransfer.OTClient;
//import com.yahoo.ycsb.ByteIterator;
//import com.yahoo.ycsb.DB;
//
//import java.lang.reflect.InvocationTargetException;
//import java.math.BigInteger;
//import java.util.*;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
///**
// *
// * @author Marcel Santos
// *
// */
//public class YCSBClient extends DB {
//
//    private int myId;
//    private RMIRuntime runtime;
//
//    public YCSBClient() {
//    }
//
//    @Override
//    public void init() {
//        Properties props = getProperties();
//        int initId = Integer.valueOf((String) props.get("smart-initkey"));
//        myId = initId;
//        int id = 11;
//        // cluster id responsible for replicating the piece of
//        // information in the partitioned object. for OTA it is 1
//        // and for OTB it is 3
//        //TODO need to make it general for other partitioned objects with multiple object fields
//        int clusterId = 2;
//        //name of the class to host
//
//        PartitionedObject o = null;
//        try {
//            o = (PartitionedObject) Class.forName("bftsmart.usecase.oblivioustransfer.OTClient").getConstructor().newInstance();
//            RMIRuntime runtime = new RMIRuntime(id, clusterId, o);
//            runtime.getObj().setRuntime(runtime);
//            runtime.start();
//            this.runtime = runtime;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //read from standard input
//        LinkedBlockingQueue<String> inputs = new LinkedBlockingQueue<>(100);
//        runtime.setInputReader(new CMDReader(inputs));
//        runtime.getInputReader().start();
//        System.out.println("YCSBKVClient. Initiated client runtime id: " + myId);
//    }
//
//    @Override
//    public int insert(String table, String key, HashMap<String, ByteIterator> values) {
//        if(values.values().size() > 1)
//        {
//            System.out.println("unsupported number of argumetns");
//            return -1;
//        }
////        Iterator<String> keys = values.keySet().iterator();
////        HashMap<String, byte[]> map = new HashMap<>();
////        while (keys.hasNext()) {
////            String field = keys.next();
////            map.put(field, values.get(field).toArray());
////        }
//        ;
//        new Integer(new BigInteger(values.values().iterator().next().toArray()).intValue())
//        YCSBMessage msg = YCSBMessage.newInsertRequest(table, key, );
//
////        byte[] reply = proxy.invokeOrdered(msg.getBytes());
//
//        ((OTClient) runtime.getObj()).transfer(Integer.valueOf(in));
//
//
//        YCSBMessage replyMsg = YCSBMessage.getObject(reply);
//        System.out.println(myId + "-" + msg + ": " + replyMsg.getResult());
//        return replyMsg.getResult();
//    }
//
//
//    @Override
//    public int delete(String arg0, String arg1) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public int read(String table, String key,
//            Set<String> fields, HashMap<String, ByteIterator> result) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public int scan(String arg0, String arg1, int arg2, Set<String> arg3,
//            Vector<HashMap<String, ByteIterator>> arg4) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public int update(String table, String key,
//            HashMap<String, ByteIterator> values) {
//        throw new UnsupportedOperationException();
//    }
//
//}
