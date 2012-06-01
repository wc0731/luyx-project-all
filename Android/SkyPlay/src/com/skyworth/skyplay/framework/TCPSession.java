package com.skyworth.skyplay.framework;

import java.io.IOException;
import java.net.Socket;

public class TCPSession {
	public interface ITCPSession {
		void onReceivePackage(TCPSession s, SkyPackage pkg);
		void onClosed(TCPSession s);
	}
	
	public String name = null;
	public String addr = null;
	
	protected Socket mSocket = null;

	protected ITCPSession mITCPSession = null;
	
	public TCPSession(Socket s, ITCPSession is) throws IOException {
		mITCPSession = is;
		mSocket = s;
		name = mSocket.getInetAddress().getHostName();
		addr = mSocket.getInetAddress().getHostAddress();
	}
	
	public void close() throws IOException {
		mSocket.close();
	}
}
