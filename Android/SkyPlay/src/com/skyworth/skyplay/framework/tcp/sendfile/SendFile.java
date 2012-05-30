package com.skyworth.skyplay.framework.tcp.sendfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SendFile {
	public static final int SERVER_PORT =23900; 
	public static final int SERVER_CLIENT =23901; 
	
	public enum COMMAND {
		START,
		SENDING,
		END,
	}
	
	public static class SendFilePackage implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4256202567324692957L;
		
		private transient static final int PACKAGE_SIZE = 4096;
		
		public COMMAND cmd = null;
		public String name = null;
		public long size = 0;
		public long sign = 0;
		public byte[] data = new byte[PACKAGE_SIZE];
		
		public SendFilePackage(COMMAND c, String n, long s, long si, byte[] d) {
			cmd = c;
			name = n;
			size = s;
			sign = si;
			for(int i = 0; i < d.length; i++)
				data[i] = d[i];
		}
		
		public static SendFilePackage toPackage(byte[] d) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(d);
				ObjectInputStream oin = new ObjectInputStream(bin);
				SendFilePackage pkg = (SendFilePackage)oin.readObject();
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
		
		public static byte[] toBytes(SendFilePackage pkg) {
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
