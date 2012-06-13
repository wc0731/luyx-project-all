package SkyPlay2.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import SkyPlay2.Config;
import SkyPlay2.NetLogic;
import SkyPlay2.SkyPackages;

public class UDPClient extends NetLogic {
	private DatagramSocket mDatagramSocket = null;
	
	private static UDPClient mUDPClient = null;
	
	private Client mClient = null;
	 
	public static UDPClient getInstance() {
		if(mUDPClient == null)
			mUDPClient = new UDPClient();
		return mUDPClient;
	}

	UDPClient() {
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
	
	public void search(Client c) {
		mClient = c;
		SkyPackages pkg = new SkyPackages();
		pkg.addr = "255.255.255.255";
		pkg.port = Config.UDP_PORT;
		pkg.name = "";
		sendPackages(pkg);
	}

	@Override
	protected void onSendPackages(SkyPackages pkg) {
		// TODO Auto-generated method stub
		try {
			System.out.println("onSendPackages!!");
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
			System.out.println("onReceivePackages!!");
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
		System.out.println("onHandlePackages!!");
		CServer server = new CServer();
		server.addr = pkg.addr;
		server.name = pkg.name;
		mClient.onUDPClientSearchResponse(server);
	}

	@Override
	protected boolean isAlive() {
		// TODO Auto-generated method stub
		return true;//!mDatagramSocket.isClosed();
	}
}
 
