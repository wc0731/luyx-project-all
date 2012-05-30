package com.skyworth.skyplay.framework.udp.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MessageService {
	public final static int PORT = 13452;
	
	public static class MessageServicePackage implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2742364445708732969L;
		
		public String data = null;
		
		public MessageServicePackage(String s) {
			data = s;
		}
		
		public static MessageServicePackage toPackage(byte[] d) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(d);
				ObjectInputStream oin = new ObjectInputStream(bin);
				MessageServicePackage pkg = (MessageServicePackage)oin.readObject();
				oin.close();
				return pkg;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return null;
		}
		
		public static byte[] toBytes(MessageServicePackage pkg) {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();   
				ObjectOutputStream oout = new ObjectOutputStream(bout);
				oout.writeObject(pkg);     
				oout.close();
				byte[] bb = bout.toByteArray();
				return bb;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null; 
		}
	}
}
