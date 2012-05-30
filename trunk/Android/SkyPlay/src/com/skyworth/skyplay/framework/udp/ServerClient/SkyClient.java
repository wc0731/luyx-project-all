package com.skyworth.skyplay.framework.udp.ServerClient;

import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import com.skyworth.skyplay.framework.Connection;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.UDP;
import com.skyworth.skyplay.framework.Util;

public class SkyClient extends UDP {
	public interface ISkyClient {
		void onSearchResponse(Connection c);
		void onSearchTimeout();
		
		void onConnectResponse(Connection c);
		void onConnectDeny(Connection c);
		void onConnectTimeout();
		
		void onDisconnetResponse(Connection c);
		void onDisconnectTimeout();
		
		void onConnectionTimeout(Connection c);
		
		void onHeartBeat(Connection c);
	}
	
	public static Connection ServerConnection = null;
	
	protected ISkyClient mISkyClient = null;

	public SkyClient(ISkyClient isc) throws SocketException {
		super(ServerClient.CLIENT_PORT);
		// TODO Auto-generated constructor stub
		mISkyClient = isc;
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(ServerConnection != null) {
					SkyPackage pkg = new SkyPackage();
					pkg.addr = ServerConnection.addr;
					pkg.port = ServerClient.SERVER_PORT;
					pkg.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.HEARTBEAT)));
					send(pkg);
				}
			}
        }, 0, 1000);
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(ServerConnection != null) {
					ServerConnection.timeout++;
					if(ServerConnection.timeout >= ServerClient.HEARTBEAT_TIMEOUT) {
						mISkyClient.onConnectionTimeout(ServerConnection);
						ServerConnection = null;
					}
				}
			}
        }, 0, 1000);
		
		Util.logger("local ip:" + Util.getLocalIP());
	}

	@Override
	protected void onSendFailed(SkyPackage pkg) {
		// TODO Auto-generated method stub
		ServerClient.ServerClientPackage pp = ServerClient.ServerClientPackage.toPackage(pkg.data);
		switch(pp.cmd) {
			case RES_SEARCH:
				mISkyClient.onSearchTimeout();
				break;
			case RES_CONNECT:
				mISkyClient.onConnectTimeout();
				break;
			case RES_DISCONNECT:
				mISkyClient.onDisconnectTimeout();
				break;
			default:
				break;
		}
	}

	@Override
	protected void onSendSuccessfully(SkyPackage pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onHandlePackage(SkyPackage pkg) {
		// TODO Auto-generated method stub
		ServerClient.ServerClientPackage pp = ServerClient.ServerClientPackage.toPackage(pkg.data);
		switch(pp.cmd) {
			case RES_SEARCH:
				mISkyClient.onSearchResponse(new Connection(pkg.name, pkg.addr));
				break;
			case RES_CONNECT:
				ServerConnection = new Connection(pkg.name, pkg.addr);
				mISkyClient.onConnectResponse(ServerConnection);
				break;
			case RES_DISCONNECT:
				mISkyClient.onDisconnetResponse(ServerConnection);
				ServerConnection = null;
				break;
			case HEARTBEAT:
				if(ServerConnection != null) {
					ServerConnection.timeout = 0;
					mISkyClient.onHeartBeat(ServerConnection);
				}
				break;
			default:
				break;
		}
	}

	public void search() {
		SkyPackage pkg = new SkyPackage();
		pkg.addr = "255.255.255.255";
		pkg.port = ServerClient.SERVER_PORT;
		pkg.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.SEARCH)));
		send(pkg);
	}
	
	public void connect(Connection c) {
		SkyPackage pkg = new SkyPackage();
		pkg.addr = c.addr;
		pkg.port = ServerClient.SERVER_PORT;
		pkg.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.CONNECT)));
		send(pkg);
	}
	
	public void disconnect() {
		if(ServerConnection != null) {
			SkyPackage pkg = new SkyPackage();
			pkg.addr = ServerConnection.addr;
			pkg.port = ServerClient.SERVER_PORT;
			pkg.setData(ServerClient.ServerClientPackage.toBytes(new ServerClient.ServerClientPackage(ServerClient.COMMAND.DISCONNECT)));
			send(pkg);
		}
	}
}
