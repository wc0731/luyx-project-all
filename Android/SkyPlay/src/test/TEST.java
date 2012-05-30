package test;

import java.net.SocketException;

import com.skyworth.skyplay.framework.Connection;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer.ISkyServer;

public class TEST implements ISkyServer {
	
	public static void main(String[] args) throws SocketException, InterruptedException {
		new TEST();
	}
	
	private SkyServer mSkyServer = null;
	
	public TEST() throws SocketException {
		mSkyServer = new SkyServer(this);
	}

	@Override
	public void onConnectionTimeout(Connection c) {
		// TODO Auto-generated method stub
		System.out.println("Connection:" + c.name + ":" + c.addr + " timeout!!!");
	}

	@Override
	public void onConnect(Connection c) {
		// TODO Auto-generated method stub
		System.out.println("Connection:" + c.name + ":" + c.addr + " connected!!!");
	}

	@Override
	public void onDisconnect(Connection c) {
		// TODO Auto-generated method stub
		System.out.println("Connection:" + c.name + ":" + c.addr + " disconnect!!!");
	}

	@Override
	public void onHeartBeat(Connection c) {
		// TODO Auto-generated method stub
		
	}
}
