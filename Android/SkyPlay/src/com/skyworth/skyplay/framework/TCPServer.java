package com.skyworth.skyplay.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.skyworth.skyplay.framework.TCPSession.ITCPSession;

public abstract class TCPServer extends ServerSocket implements ITCPSession {
	public interface ITCPServer {
		void onNewTCPSession(TCPServer server, Socket s);
		void onTCPSessionClosed(TCPSession s);
	}
	
	private ITCPServer mITCPServer = null;

	public TCPServer(int port, ITCPServer is) throws IOException {
		super(port);
		// TODO Auto-generated constructor stub
		mITCPServer = is;
		listener.start();
	}
	
	public void onDestroy() throws IOException {
		close();
	}
	
	private Thread listener = new Thread() {
		public void run() {
			try {
				while(true)
					mITCPServer.onNewTCPSession(TCPServer.this, accept());
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onClosed(TCPSession s) {
		// TODO Auto-generated method stub
		mITCPServer.onTCPSessionClosed(s);
	}
}
