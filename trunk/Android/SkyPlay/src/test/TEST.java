package test;

import java.net.SocketException;
import java.util.ArrayList;

import com.skyworth.skyplay.framework.Session;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer.ISkyServer;

public class TEST implements ISkyServer {

	@Override
	public void onConnect(Session c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect(Session c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHeartBeat(Session c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int chkConnection(String name, String addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onClientTimeout(Session s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSessionConnectionChanged(ArrayList<Session> list) {
		// TODO Auto-generated method stub
		
	}

}
