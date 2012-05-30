package com.skyworth.skyplay.framework;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	public interface ITCPClient {
		void onSocketClosed(TCPClient c);
		void onReceivePackage(SkyPackage pkg);
	}
	
	/*protected Socket mSocket = null;
	protected DataInputStream mDataInputStream = null;
	protected DataOutputStream mDataOutputStream = null;
	
	protected ITCPClient mITCPClient = null;
	
	public TCPClient(ITCPClient itc, String ip, int port) throws UnknownHostException, IOException {
		mITCPClient = itc;
		mSocket = new Socket(ip, port);
		mDataInputStream = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
		mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
		receiver.start();
	}
	
	public TCPClient(ITCPClient itc, Socket s) throws IOException {
		mITCPClient = itc;
		mSocket = s;
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
					mITCPClient.onReceivePackage(pkg);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	};*/
}
