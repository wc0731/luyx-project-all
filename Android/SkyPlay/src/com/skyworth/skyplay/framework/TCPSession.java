package com.skyworth.skyplay.framework;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
	protected DataInputStream mDataInputStream = null;
	protected DataOutputStream mDataOutputStream = null;
	
	private ITCPSession mITCPSession = null;
	
	public TCPSession(Socket s, ITCPSession is) throws IOException {
		mITCPSession = is;
		mSocket = s;
		name = mSocket.getInetAddress().getHostName();
		addr = mSocket.getInetAddress().getHostAddress();
		mDataInputStream = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
		mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
		receiver.start();
	}
	
	public void send(SkyPackage pkg) throws IOException {
		mDataOutputStream.write(SkyPackage.toBytes(pkg));
		mDataOutputStream.flush();
	}
	
	public void close() throws IOException {
		mSocket.close();
	}
	
	private Thread receiver = new Thread() {
		public void run() {
			try {
				while(!mSocket.isClosed()) {
					byte[] recvBuf = new byte[SkyPackage.SIZE];
					mDataInputStream.read(recvBuf);
					SkyPackage pkg = SkyPackage.toPackage(recvBuf);
					pkg.name = mSocket.getInetAddress().getHostName();
					pkg.addr = mSocket.getInetAddress().getHostAddress();
					pkg.port = mSocket.getPort();
					mITCPSession.onReceivePackage(TCPSession.this, pkg);
				}
				mITCPSession.onClosed(TCPSession.this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	};
}
