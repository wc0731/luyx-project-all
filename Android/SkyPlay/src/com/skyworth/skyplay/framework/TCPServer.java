package com.skyworth.skyplay.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.skyworth.skyplay.framework.TCPConnection.ITCPConnection;

public abstract class TCPServer extends ServerSocket implements ITCPConnection {
	protected ArrayList<TCPConnection> connectionList = new ArrayList<TCPConnection>();

	public TCPServer(int port) throws IOException {
		super(port);
		// TODO Auto-generated constructor stub
		listener.start();
	}
	
	public void closeAllConnction() throws IOException {
		for(int i = 0; i < connectionList.size(); i++)
			connectionList.get(i).close();
	}
	
	public void onDestroy() throws IOException {
		close();
	}
	
	protected void onNewTCPConnection(TCPConnection c) {
		connectionList.add(c);
	}
	
	private Thread listener = new Thread() {
		public void run() {
			try {
				while(true) {
					Socket s = accept();
					TCPConnection c = new TCPConnection(s.getInetAddress().getHostName(), s.getInetAddress().getHostAddress(), s, TCPServer.this);
					onNewTCPConnection(c);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onClosed(TCPConnection c) {
		// TODO Auto-generated method stub
		if(connectionList.contains(c))
			connectionList.remove(c);
	}
}
