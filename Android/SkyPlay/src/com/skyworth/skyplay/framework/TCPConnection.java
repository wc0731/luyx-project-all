package com.skyworth.skyplay.framework;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPConnection extends Connection {
	public interface ITCPConnection {
		void onReceivePackage(TCPConnection c, SkyPackage pkg);
		void onClosed(TCPConnection c);
	}
	
	protected Socket mSocket = null;
	protected DataInputStream mDataInputStream = null;
	protected DataOutputStream mDataOutputStream = null;
	
	protected ITCPConnection mITCPConnection = null;

	public TCPConnection(String n, String a, Socket s, ITCPConnection ii) throws IOException {
		super(n, a);
		// TODO Auto-generated constructor stub
		mITCPConnection = ii;
		mSocket = s;
		mDataInputStream = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
		mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
		receiver.start();
	}

	public TCPConnection(String n, String a, int port, ITCPConnection ii) throws IOException {
		super(n, a);
		// TODO Auto-generated constructor stub
		mITCPConnection = ii;
		mSocket = new Socket(a, port);
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
					mITCPConnection.onReceivePackage(TCPConnection.this, pkg);
				}
				mITCPConnection.onClosed(TCPConnection.this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	};
}
