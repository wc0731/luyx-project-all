package com.skyworth.skyplay.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import com.skyworth.skyplay.framework.TCPClient.ITCPClient;

public abstract class TCPServer {
/*	
	protected ArrayList<TCPClient> clientList = new ArrayList<TCPClient>();

	public TCPServer(int port) throws IOException {
		super(port);
		// TODO Auto-generated constructor stub
		
		listener.start();
	}
	
	private Thread listener = new Thread() {
		public void run() {
			while(true) {
				try {
					clientList.add(new TCPClient(TCPServer.this, TCPServer.this.accept()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void onSocketClosed(TCPClient c) {
		// TODO Auto-generated method stub
		if(clientList.contains(c))
			clientList.remove(c);
	}*/
}
