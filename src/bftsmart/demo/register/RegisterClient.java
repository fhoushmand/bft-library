//package bftsmart.demo.register;
//
//import bftsmart.demo.map.MapRequestType;
//import bftsmart.tom.ServiceProxy;
//
//import java.io.*;
//import java.util.*;
//
//public class RegisterClient<T>{
//
//	public static void main(String[] args) {
//		RegisterClient<Integer> client = new RegisterClient<>(0, 1);
//		client.write(1);
////		System.out.println(client.read());
////		client.write(2);
////		client.write(3);
////		System.out.println(client.read());
//	}
//
//	ServiceProxy serviceProxy;
//
//	HashMap<String,T> cachedInvocations;
//
//
//	public RegisterClient(int clientId, int clusterId)
//	{
//		cachedInvocations = new HashMap<>();
//		serviceProxy = new ServiceProxy(clientId, clusterId);
//	}
//
//	public T write(T newVal) {
//		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//				ObjectOutput objOut = new ObjectOutputStream(byteOut);) {
//
//			objOut.writeObject(RegisterRequestType.WRITE);
//			objOut.writeObject(newVal);
//			objOut.flush();
//			byteOut.flush();
//
//			byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
//			if (reply.length == 0)
//				return null;
//			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
//					ObjectInput objIn = new ObjectInputStream(byteIn)) {
//				return (T)objIn.readObject();
//			}
//
//		} catch (IOException | ClassNotFoundException e) {
//			System.out.println("Exception writing value into register: " + e.getMessage());
//		}
//		return null;
//	}
//
//	public T read() {
//		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
//
//			 objOut.writeObject(RegisterRequestType.READ);
//
//		 	 objOut.flush();
//			 byteOut.flush();
//
//			 byte[] reply = serviceProxy.invokeUnordered(byteOut.toByteArray());
//			 if (reply.length == 0)
//			 	 return null;
//			 try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
//				  ObjectInput objIn = new ObjectInputStream(byteIn)) {
//			 	return (T)objIn.readObject();
//			 }
//
//		} catch (IOException | ClassNotFoundException e) {
//			System.out.println("Exception getting value from register: " + e.getMessage());
//		}
//		return null;
//	}
//}