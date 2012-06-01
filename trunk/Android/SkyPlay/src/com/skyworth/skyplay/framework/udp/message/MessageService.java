package com.skyworth.skyplay.framework.udp.message;

import java.io.Serializable;

import com.skyworth.skyplay.framework.Packages;

public class MessageService {
	public final static int PORT = 13452;
	
	public static class MessageServicePackage extends Packages implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -3203605251942494371L;
		public String data = null;
		
		public MessageServicePackage(String s) {
			data = s;
		}
	}
}
