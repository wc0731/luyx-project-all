package SkyPlay2.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


import SkyPlay2.Config;
import SkyPlay2.NetLogic;
import SkyPlay2.SkyPackages;

public class UDPServer extends NetLogic {
	private DatagramSocket mDatagramSocket = null;
	
	private static UDPServer mUDPServer = null;
	 
	public static UDPServer getInstance() {
		if(mUDPServer == null)
			mUDPServer = new UDPServer();
		return mUDPServer;
	}

	UDPServer() {
		super();
		// TODO Auto-generated constructor stub
		try {
			mDatagramSocket = new DatagramSocket(Config.UDP_PORT);
			start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onSendPackages(SkyPackages pkg) {
		// TODO Auto-generated method stub
		try {
			mDatagramSocket.send(SkyPackages.toDatagramPacket(pkg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected SkyPackages onReceivePackages() {
		// TODO Auto-generated method stub
        try {
			byte[] recvBuf = new byte[SkyPackages.size()];
	        DatagramPacket recvPacket = new DatagramPacket(recvBuf, SkyPackages.size());
			mDatagramSocket.receive(recvPacket);
			if(!getLocalIP().equals(recvPacket.getAddress().getHostAddress())) {
				SkyPackages pkg = (SkyPackages)SkyPackages.toPackages(recvBuf);
				pkg.name = recvPacket.getAddress().getHostName();
				pkg.addr = recvPacket.getAddress().getHostAddress();
				pkg.port = recvPacket.getPort();
				return pkg;
			}
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}

	@Override
	protected void onHandlePackages(SkyPackages pkg) {
		// TODO Auto-generated method stub
		sendPackages(pkg);
	}

	@Override
	protected boolean isAlive() {
		// TODO Auto-generated method stub
		return true;//!mDatagramSocket.isClosed();
	}
}
 
