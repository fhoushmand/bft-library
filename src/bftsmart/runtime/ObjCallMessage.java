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


public class ObjCallMessage extends RTMessage implements Externalizable, Comparable {

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

	public String getCallerName() { return new String(caller); }


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
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);

		out.writeInt(caller.length);
		out.write(caller);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);

		//read caller name in bytes
		int callerNameSize = in.readInt();
		caller = new byte[callerNameSize];
		in.readFully(caller);
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
}
