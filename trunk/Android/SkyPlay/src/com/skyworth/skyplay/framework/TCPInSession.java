package com.skyworth.skyplay.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TCPInSession extends TCPSession {
	protected ObjectInputStream mObjectInputStream = null;

	public TCPInSession(Socket s, ITCPSession is) throws IOException {
		super(s, is);
		// TODO Auto-generated constructor stub
		mObjectInputStream = new ObjectInputStream(mSocket.getInputStream());
		receiver.start();
	}

	
	private Thread receiver = new Thread() {
		public void run() {
			try {
				while(!mSocket.isClosed()) {
					
					/*byte[] recvBuf = new byte[SkyPackage.SIZE];
					mDataInputStream.read(recvBuf);
					SkyPackage pkg = (SkyPackage)SkyPackage.toPackage(recvBuf);*/
					SkyPackage pkg = (SkyPackage)mObjectInputStream.readObject();
					if(pkg != null) {
						pkg.name = mSocket.getInetAddress().getHostName();
						pkg.addr = mSocket.getInetAddress().getHostAddress();
						pkg.port = mSocket.getPort();
						
						System.out.println("name:" + pkg.name + " addr:" + pkg.addr + " port:" + pkg.port + " len:" + pkg.len);
						mITCPSession.onReceivePackage(TCPInSession.this, pkg);
					}
				}
				mITCPSession.onClosed(TCPInSession.this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	};
}
