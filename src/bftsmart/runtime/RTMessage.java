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

import bftsmart.tom.util.TOMUtil;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * This is the super-class for all other kinds of messages created by JBP
 * 
 */

public abstract class RTMessage implements Externalizable {

    protected int sender; // ID of the process which sent the message
    public transient boolean authenticated; // set to TRUE if the message was received
                                            // with a (valid) mac, FALSE if no mac was given
                                            // note that if the message arrives with an
                                            // invalid MAC, it won't be delivered

    //the fields bellow are not serialized!!!

    public transient long timestamp = 0; // timestamp to be used by the application

    public transient int destination = -1; // message destination
    public transient boolean signed = false; // is this message signed?

    public transient long receptionTime;//the reception time of this message (nanoseconds)
    public transient long receptionTimestamp;//the reception timestamp of this message (miliseconds)

    public transient boolean timeout = false;//this message was timed out?

    public transient boolean recvFromClient = false; // Did the client already sent this message to me, or did it arrived in the batch?
    public transient boolean isValid = false; // Was this request already validated by the replica?

    //the bytes received from the client and its MAC and signature
    public transient byte[] serializedMessage = null;
    public transient byte[] serializedMessageSignature = null;

    /**
     * Creates a new instance of SystemMessage
     */
    public RTMessage(){}

    /**
     * Creates a new instance of SystemMessage
     * @param sender ID of the process which sent the message
     */
    public RTMessage(int sender){
        this.sender = sender;
    }
    
    /**
     * Returns the ID of the process which sent the message
     * @return
     */
    public final int getSender() {
        return sender;
    }

    // This methods implement the Externalizable interface
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(sender);
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        sender = in.readInt();
    }

    public void wExternal(DataOutput out) throws IOException {
        out.writeInt(sender);
    }

    public void rExternal(DataInput in) throws IOException {
        sender = in.readInt();
    }
}
