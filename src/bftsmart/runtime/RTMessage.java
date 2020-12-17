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
import bftsmart.tom.util.TOMUtil;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * This class represents a total ordered message
 */
public class RTMessage extends SystemMessage implements Externalizable, Comparable, Cloneable {

	// method name
	private byte[] method;

	// method argument
	// TODO multiple args
	private Object arg;

	// method id
	private byte[] operationId;

	//currently not used
	private byte[] content = null; // Content of the message


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


	public RTMessage() {
	}


	/**
	 * Creates a new instance of RTMessage. This one has an operationId parameter
	 * used for FIFO executions
	 * @param sender The client processNumber
//	 * @param sequence The sequence number created based on the message type
	 * @param operationId The operation sequence number disregarding message type
	 * @param content The command to be executed
	 */
	public RTMessage(int sender, byte[] operationId, byte[] method, Object arg, byte[] content) {
		super(sender);
		this.method = method;
		this.operationId = operationId;
		this.arg = arg;
		this.content = content;
	}

	public String getMethodName() { return new String(method); }

	public Object getArg() { return arg; }

	/**
	 * Retrieves the sequence number defined by the client
	 * @return The sequence number defined by the client
	 */
//	public int getSequence() {
//		return sequence;
//	}
	
	public String getOperationId() {
		return new String(operationId);
	}


//	/**
//	 * Retrieves the ID for this message. It should be unique
//	 * @return The ID for this message.
//	 */
//	public int getId() {
//		return id;
//	}

	/**
	 * Retrieves the content of the message
	 * @return The content of the message
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Verifies if two RTMessage are equal. For performance reasons, the method
	 * only verifies if the send and sequence are equal.
	 *
	 * Two RTMessage are equal if they have the same sender, sequence number
	 * and content.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof RTMessage)) {
			return false;
		}

		RTMessage mc = (RTMessage) o;
//		return (mc.getSender() == sender) && (mc.getSequence() == sequence) && (mc.getOperationId() == operationId);
		//changed
		return mc.toString().equals(toString());
	}

	@Override
	public int hashCode() {
		/*int hash = 5;
		hash = 59 * hash + this.sequence;
		hash = 59 * hash + this.getSender();
		hash = 59 * hash + this.getOperationId();*/
		return toString().hashCode();
	}

	@Override
	public String toString() {
		String m = new String(method);
		String id = new String(operationId);
		return "[" + m + ":" + id + ":" + arg + "]";
	}

	public void wExternal(DataOutput out) throws IOException {
		out.writeInt(sender);

		//write method name in bytes
		out.writeInt(method.length);
		out.write(method);
		//write argument obj
		out.writeInt(TOMUtil.getBytes(arg).length);
		out.write(TOMUtil.getBytes(arg));

		out.writeInt(operationId.length);
		out.write(operationId);
		
		if (content == null) {
			out.writeInt(-1);
		} else {
			out.writeInt(content.length);
			out.write(content);
		}
	}

	public void rExternal(DataInput in) throws IOException, ClassNotFoundException {
		sender = in.readInt();

		//read method name in bytes
		int methodNameSize = in.readInt();
		method = new byte[methodNameSize];
		in.readFully(method);
		//read argument object
		int objectSize = in.readInt();
		byte[] objectBytes = new byte[objectSize];
		in.readFully(objectBytes);
		arg = TOMUtil.getObject(objectBytes);

		int opIdSize = in.readInt();
		operationId = new byte[opIdSize];
		in.readFully(operationId);

		int toRead = in.readInt();
		if (toRead != -1) {
			content = new byte[toRead];
			in.readFully(content);
		}
	}

	/**
	 * Used to build an unique processNumber for the message
	 */
//	private void buildIdentifier() {
//		//processNumber = (sender << 20) | sequence;
//		int hash = 5;
//		hash = 59 * hash + this.getSender(); //sender
//		hash = 59 * hash + method;
//		hash = 59 * hash + this.sequence;
//		id = hash;
//	}

	/**
	 * Used to build an unique processNumber for the message
	 */
//	 private void buildId() {
//		 //processNumber = (sender << 20) | sequence;
//             	int hash = 5;
// 		hash = 59 * hash + this.getSender();
////		hash = 59 * hash + this.sequence;
//		id = hash;
//	 }

//	 /**
//	  * Retrieves the process ID of the sender given a message ID
//	  * @param id Message ID
//	  * @return Process ID of the sender
//	  */
//	 public static int getSenderFromId(int id) {
//		 return id >>> 20;
//	 }

	 public static byte[] messageToBytes(RTMessage m) {
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 DataOutputStream dos = new DataOutputStream(baos);
		 try{
			 m.wExternal(dos);
			 dos.flush();
		 }catch(Exception e) {
		 }
		 return baos.toByteArray();
	 }

	 public static RTMessage bytesToMessage(byte[] b) {
		 ByteArrayInputStream bais = new ByteArrayInputStream(b);
		 DataInputStream dis = new DataInputStream(bais);

		 RTMessage m = new RTMessage();
		 try{
			 m.rExternal(dis);
		 }catch(Exception e) {
			 LoggerFactory.getLogger(RTMessage.class).error("Failed to deserialize RTMessage",e);
			 return null;
		 }

		 return m;
	 }

	 //TODO unchecked
	 @Override
	 public int compareTo(Object o) {
		 final int BEFORE = -1;
		 final int EQUAL = 0;
		 final int AFTER = 1;

		 RTMessage tm = (RTMessage)o;

		 if (this.equals(tm))
			 return EQUAL;

		 if (this.getSender() < tm.getSender())
			 return BEFORE;
		 if (this.getSender() > tm.getSender())
			 return AFTER;

//		 if (this.getSequence() < tm.getSequence())
//			 return BEFORE;
//		 if (this.getSequence() > tm.getSequence())
//			 return AFTER;

//		 if(this.getOperationId() < tm.getOperationId())
//			 return BEFORE;
//		 if(this.getOperationId() > tm.getOperationId())
//			 return AFTER;

		 return EQUAL;
	 }
	 
        @Override
	 public Object clone() throws CloneNotSupportedException {
             
                          
                    RTMessage clone = new RTMessage(sender,
                            operationId, method, arg, content);


                    clone.authenticated = this.authenticated;
                    clone.destination = this.destination;
                    clone.isValid = this.isValid;
                    clone.receptionTime = this.receptionTime;
                    clone.receptionTimestamp = this.receptionTimestamp;
                    clone.recvFromClient = this.recvFromClient;
                    clone.serializedMessage = this.serializedMessage;
                    clone.serializedMessageSignature = this.serializedMessageSignature;
                    clone.signed = this.signed;
                    clone.timeout = this.timeout;
                    clone.timestamp = this.timestamp;

                    return clone;
                        
		}
}
