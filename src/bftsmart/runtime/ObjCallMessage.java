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


public class ObjCallMessage extends RTMessage implements Externalizable, Comparable, Cloneable {

	// method name
	protected byte[] method;

	// method argument
	// TODO multiple args
	protected Object arg;

	// obj call id
	private byte[] operationId;

	// n
	private int n;

	// calling method name
	protected byte[] caller;


	public ObjCallMessage() {
	}


	/**
	 * Creates a new instance of RTMessage. This one has an operationId parameter
	 * used for FIFO executions
	 * @param sender The client processNumber
	 */
	public ObjCallMessage(int sender, byte[] operationId, byte[] method, Object arg, byte[] caller) {
		super(sender);
		this.method = method;
		this.operationId = operationId;
		this.arg = arg;
		this.caller = caller;
	}

	public Object getArg() {
		return arg;
	}

	public String getMethodName() { return new String(method); }

	public String getCallerName() { return new String(caller); }

	public String getOperationId() {
		return new String(operationId);
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getN() {
		return n;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof ObjCallMessage)) {
			return false;
		}

		ObjCallMessage mc = (ObjCallMessage) o;
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
//		String args = "(";
//		for(int i = 0; i < ((Object[])arg).length; i++) {
//			args += ((Object[])arg)[i].toString();
//			if (i != ((Object[]) arg).length - 1)
//				args += ",";
//		}
//		args += ")";
		return "[" + m + "_" + id + "_" + arg + "]";
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);

		//write method name in bytes
		out.writeInt(method.length);
		out.write(method);
		//write argument obj
		out.writeInt(TOMUtil.getBytes(arg).length);
		out.write(TOMUtil.getBytes(arg));

		out.writeInt(operationId.length);
		out.write(operationId);
		out.writeInt(n);

		//write method name in bytes
		out.writeInt(caller.length);
		out.write(caller);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);

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
		n = in.readInt();

		//read caller name in bytes
		int callerNameSize = in.readInt();
		caller = new byte[callerNameSize];
		in.readFully(caller);
	}

//	@Override
//	public void wExternal(DataOutput out) throws IOException {
//		super.wExternal(out);
//
//		//write method name in bytes
//		out.writeInt(method.length);
//		out.write(method);
//		//write argument obj
//		out.writeInt(TOMUtil.getBytes(arg).length);
//		out.write(TOMUtil.getBytes(arg));
//	}
//
//	@Override
//	public void rExternal(DataInput in) throws IOException {
//		super.rExternal(in);
////
//		//read method name in bytes
//		int methodNameSize = in.readInt();
//		method = new byte[methodNameSize];
//		in.readFully(method);
//		//read argument object
//		int objectSize = in.readInt();
//		byte[] objectBytes = new byte[objectSize];
//		in.readFully(objectBytes);
//		arg = TOMUtil.getObject(objectBytes);
//	}

	public static byte[] messageToBytes(ObjCallMessage m) {
		byte[] data = null;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream dos = new ObjectOutputStream(baos);
			m.wExternal(dos);
			dos.flush();
			data = baos.toByteArray();
			m.serializedMessage = data;
		}catch(Exception e) {
			LoggerFactory.getLogger(ObjCallMessage.class).error("Failed to serialize ObjCallMessage",e);
			return null;
		}
		return data;
	}

	 public static ObjCallMessage bytesToMessage(byte[] b) {
		 ObjCallMessage m = new ObjCallMessage();
		 try{
			 ByteArrayInputStream bais = new ByteArrayInputStream(b);
			 ObjectInputStream dis = new ObjectInputStream(bais);
			 m.rExternal(dis);
		 }catch(Exception e) {
			 LoggerFactory.getLogger(ObjCallMessage.class).error("Failed to deserialize ObjCallMessage",e);
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

		 ObjCallMessage tm = (ObjCallMessage)o;

		 if (this.equals(tm))
			 return EQUAL;

		 if (this.getSender() < tm.getSender())
			 return BEFORE;
		 if (this.getSender() > tm.getSender())
			 return AFTER;
		 return EQUAL;
	 }

	@Override
	public Object clone() throws CloneNotSupportedException {


		ObjCallMessage clone = new ObjCallMessage(sender, operationId, method, arg, caller);


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
