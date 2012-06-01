package com.skyworth.skyplay.framework;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPOutSession extends TCPSession {

	protected ObjectOutputStream mObjectOutputStream = null;
	
	public TCPOutSession(Socket s, ITCPSession is) throws IOException {
		super(s, is);
		// TODO Auto-generated constructor stub
		mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
	}
	
	public void send(SkyPackage pkg) throws IOException {
		mObjectOutputStream.writeObject(pkg);
		mObjectOutputStream.flush();
	}
}
