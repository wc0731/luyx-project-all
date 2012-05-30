package com.skyworth.skyplay.framework.udp.ServerClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ServerClient {
	public final static int SERVER_PORT = 12888;
	public final static int CLIENT_PORT = 12889;
	
	public final static int HEARTBEAT_TIMEOUT = 30;
	public final static int SERVER_MAX_CONNECTION = 10;

	public enum COMMAND {
		SEARCH,
		CONNECT,
		DISCONNECT,
		
		RES_SEARCH,
		RES_CONNECT,
		RES_DISCONNECT,
		
		HEARTBEAT,
	}
	
	public static class ServerClientPackage implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8948320262232127726L;
		
		public COMMAND cmd;
		
		public ServerClientPackage(COMMAND c) {
			cmd = c;
		}
		
		public static ServerClientPackage toPackage(byte[] d) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(d);
				ObjectInputStream oin = new ObjectInputStream(bin);
				ServerClientPackage pkg = (ServerClientPackage)oin.readObject();
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
		
		public static byte[] toBytes(ServerClientPackage pkg) {
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
