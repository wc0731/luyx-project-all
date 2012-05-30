package com.skyworth.skyplay.framework.udp.ServerClient;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.skyworth.skyplay.framework.Connection;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.UDP;
import com.skyworth.skyplay.framework.Util;


public class SkyServer extends UDP {

	public interface ISkyServer {
		void onConnectionTimeout(Connection c);
		
		void onConnect(Connection c);
		int chkConnection(Connection c);
		void onDisconnect(Connection c);
		
		void onHeartBeat(Connection c);
	}

	protected ArrayList<Connection> readyClientConnection = new ArrayList<Connection>();
	public static ArrayList<Connection> ClientConnection = new ArrayList<Connection>();
	private ISkyServer mISkyServer = null;
	
	public SkyServer(ISkyServer isc) throws SocketException {
		super(ServerClient.SERVER_PORT);
		// TODO Auto-generated constructor stub
		mISkyServer = isc;
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i = 0; i < ClientConnection.size(); i++) {
					SkyPackage pkg = new SkyPackage();
					pkg.addr = ClientConnection.get(i).addr;
					pkg.port = ServerClient.CLIENT_PORT;
					pkg.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.HEARTBEAT)));
					send(pkg);
				}
			}
        }, 0, 1000);
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i = 0; i < ClientConnection.size(); i++) {
					ClientConnection.get(i).timeout++;
					if(ClientConnection.get(i).timeout >= ServerClient.HEARTBEAT_TIMEOUT) {
						mISkyServer.onConnectionTimeout(ClientConnection.get(i));
						ClientConnection.remove(i);
					}
				}
			}
        }, 0, 1000);
		Util.logger("local ip:" + Util.getLocalIP());
		Util.logger("SkyPackage.SIZE:" + SkyPackage.SIZE);
	}

	@Override
	protected void onSendFailed(SkyPackage pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSendSuccessfully(SkyPackage pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onHandlePackage(SkyPackage pkg) {
		// TODO Auto-generated method stub
		ServerClient.ServerClientPackage pp = ServerClient.ServerClientPackage.toPackage(pkg.data);
		SkyPackage pkgpp = new SkyPackage();
		Connection c = new Connection(pkg.name, pkg.addr);
		Util.logger("onHandlePackage: name:" + pkg.name + " addr:" + pkg.addr + "  cmd:" + pp.cmd);
		switch(pp.cmd) {
			case SEARCH:
				pkgpp.addr = pkg.addr;
				pkgpp.port = ServerClient.CLIENT_PORT;
				pkgpp.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.RES_SEARCH)));
				send(pkgpp);
				break;
			case CONNECT:
				int ret = mISkyServer.chkConnection(c);
				if(ret == 0) {
					pkgpp.addr = pkg.addr;
					pkgpp.port = ServerClient.CLIENT_PORT;
					pkgpp.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.RES_CONNECT)));
					send(pkgpp);
					
					for(int i = 0; i < readyClientConnection.size(); i++) {
						if(readyClientConnection.get(i).equals(c))
							return;
					}
					readyClientConnection.add(c);
				}
				else {
					pkgpp.addr = pkg.addr;
					pkgpp.port = ServerClient.CLIENT_PORT;
					pkgpp.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.RES_CONNECT_DENY, ret)));
					send(pkgpp);
				}
				break;
			case DISCONNECT:
				pkgpp.addr = pkg.addr;
				pkgpp.port = ServerClient.CLIENT_PORT;
				pkgpp.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.RES_DISCONNECT)));
				send(pkgpp);
				mISkyServer.onDisconnect(c);
				break;
			case HEARTBEAT:
				for(int i = 0; i < ClientConnection.size(); i++) {
					if(ClientConnection.get(i).equals(c)) {
						ClientConnection.get(i).timeout = 0;
						mISkyServer.onHeartBeat(c);
						return;
					}
				}
				for(int i = 0; i < readyClientConnection.size(); i++) {
					if(readyClientConnection.get(i).equals(c)) {
						readyClientConnection.remove(i);
						ClientConnection.add(c);
						mISkyServer.onConnect(c);
						return;
					}
				}
				break;
			default:
				break;
		}
	}
	
	public void disconnectClient(Connection c) {
		SkyPackage pkg = new SkyPackage();
		pkg.addr = c.addr;
		pkg.port = ServerClient.CLIENT_PORT;
		pkg.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.RES_DISCONNECT)));
		send(pkg);
	}
}
