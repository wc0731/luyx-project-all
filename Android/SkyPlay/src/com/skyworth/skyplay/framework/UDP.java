package com.skyworth.skyplay.framework;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UDP extends DatagramSocket {
	public final static int DEFAULT_TIMEOUT = 30;
	
	private LinkedList<SkyPackage> recvPkgList = new LinkedList<SkyPackage>();
	private LinkedList<SkyPackage> hasSendPkgList = new LinkedList<SkyPackage>();
	
	protected abstract void onSendFailed(SkyPackage pkg);
	protected abstract void onSendSuccessfully(SkyPackage pkg);
	protected abstract void onHandlePackage(SkyPackage pkg);
	
	public UDP(int port) throws SocketException {
		super(port);
		// TODO Auto-generated constructor stub
		/*recvThread.setDaemon(true);
		sendThread.setDaemon(true);
		handleThread.setDaemon(true);*/
		recvThread.start();
		handleThread.start();
		
        new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i = 0; i < hasSendPkgList.size(); i++) {
					hasSendPkgList.get(i).timeout++;
					if(hasSendPkgList.get(i).timeout >= DEFAULT_TIMEOUT) {
						onSendFailed(hasSendPkgList.get(i));
						hasSendPkgList.remove(i);
					}
				}
			}
        }, 0, 1000);
	}
	
	public void send(SkyPackage pkg) {
		try {
			hasSendPkgList.add(pkg);
			send(SkyPackage.toDatagramPacket(pkg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onDestroy() {
		close();
	}
	
	private void handle(SkyPackage pkg) {
		Util.logger("handle package!!");
		if(!pkg.isReceipt) {
			pkg.isReceipt = true;
			send(pkg);
			onHandlePackage(pkg);
		}
		else {
			for(int i = 0; i < hasSendPkgList.size(); i++) {
				if(hasSendPkgList.get(i).equals(pkg)) {
					onSendSuccessfully(pkg);
					hasSendPkgList.remove(i);
					break;
				}
			}
		}
	}

	private Thread recvThread = new Thread() {
    	public void run() {
    		try {
				while(true) {
					Util.logger("waiting for package!!");
					byte[] recvBuf = new byte[SkyPackage.SIZE];
			        DatagramPacket recvPacket = new DatagramPacket(recvBuf, SkyPackage.SIZE);
					receive(recvPacket);
					Util.logger("package incoming!!");
					if(!Util.getLocalIP().equals(recvPacket.getAddress().getHostAddress())) {
						SkyPackage pkg = SkyPackage.toPackage(recvBuf);
						pkg.name = recvPacket.getAddress().getHostName();
						pkg.addr = recvPacket.getAddress().getHostAddress();
						pkg.port = recvPacket.getPort();
						recvPkgList.add(pkg);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    };

	private Thread handleThread = new Thread() {
		public void run() {
			boolean t = false;
			try {
				while(true) {
					if(recvPkgList.size() > 0) {
						handle(recvPkgList.poll());
						t = false;
					} 
					else
						t = true;
					if(t)
						Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
