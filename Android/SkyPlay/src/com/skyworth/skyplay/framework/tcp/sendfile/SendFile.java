package com.skyworth.skyplay.framework.tcp.sendfile;

import java.io.DataOutputStream;
import java.io.Serializable;

import com.skyworth.skyplay.framework.Packages;

public class SendFile {
	public static final int PORT =23900; 

	public static class Task {
		public long id = 0;
		
		public String name = null;
		public long size = 0;
		public long progress = 0;
		
		DataOutputStream fileOut = null;
	}
	
	public static class SendFilePackage extends Packages implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -7071752343499922022L;

		public transient static final int PACKAGE_SIZE = 4096;
		
		public COMMAND cmd = COMMAND.START;
		public String name = "";
		public long id = 0;
		public long size = 0;
		public long progress = 0;
		
		public int len = 0;
		public byte[] data = new byte[PACKAGE_SIZE];
		
		public enum COMMAND {
			START,
			SENDING,
			END,
			STOP,
		}
		
		public SendFilePackage(COMMAND c, Task t, int l, byte[] d) {
			cmd = c;
			id = t.id;
			name = t.name;
			size = t.size;
			if(d != null) {
				len = l;
				for(int i = 0; i < d.length; i++)
					data[i] = d[i];
			}
		}
	}
}
