package com.skyworth.skyplay.framework.udp.ServerClient;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import com.skyworth.skyplay.framework.Service.IServiceClient;
import com.skyworth.skyplay.framework.Session;
import com.skyworth.skyplay.framework.Session.ISession;
import com.skyworth.skyplay.framework.Session.SessionPackage;
import com.skyworth.skyplay.framework.TCPServer.ITCPServer;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.TCPServer;
import com.skyworth.skyplay.framework.TCPSession;
import com.skyworth.skyplay.framework.UDP;

public class SkyClient extends UDP implements ISession, IServiceClient, ITCPServer {
	public interface ISkyClient {
		void onSearchResponse(String name, String addr);
		void onSearchTimeout();
		
		void onConnectResponse(String name, String addr, int info);
		void onConnectTimeout();
		
		void onDisconnetResponse(Session c);
		void onDisconnectTimeout();
		
		void onHeartBeat(Session c);
		
		void onServerTimeout(Session c);
	}
	
	public static Session serverSession = null;
	
	protected ISkyClient mISkyClient = null;
	
	private Timer heartbeatTimer = null;

	public SkyClient(ISkyClient isc) throws SocketException {
		super(ServerClient.PORT);
		// TODO Auto-generated constructor stub
		mISkyClient = isc;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			disconnect();
			if(serverSession != null)
				serverSession.close();
			super.onDestroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onSendFailed(SkyPackage pkg) {
		// TODO Auto-generated method stub
		SessionPackage pp = (SessionPackage)SessionPackage.toPackage(pkg.data);
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
		SessionPackage pp = (SessionPackage)SessionPackage.toPackage(pkg.data);
		switch(pp.cmd) {
			case RES_SEARCH:
				mISkyClient.onSearchResponse(pkg.name, pkg.addr);
				break;
			case RES_CONNECT:
				if(pp.info == 0) {
					serverSession = new Session(pkg.name, pkg.addr, this);
					heartbeatTimer = new Timer();
					heartbeatTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							SkyPackage pkg = new SkyPackage();
							pkg.addr = serverSession.addr;
							pkg.port = ServerClient.PORT;
							pkg.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.HEARTBEAT, 0)));
							send(pkg);
						}
			        }, 0, 1000);
				}
				mISkyClient.onConnectResponse(pkg.name, pkg.addr, pp.info);
				break;
			case RES_DISCONNECT:
				if(heartbeatTimer != null)
					heartbeatTimer.cancel();
				mISkyClient.onDisconnetResponse(serverSession);
				serverSession = null;
				break;
			case HEARTBEAT:
				if(serverSession != null) {
					serverSession.heartbeat();
					mISkyClient.onHeartBeat(serverSession);
				}
				break;
			default:
				break;
		}
	}

	public void search() {
		if(serverSession == null) {
			SkyPackage pkg = new SkyPackage();
			pkg.addr = "255.255.255.255";
			pkg.port = ServerClient.PORT;
			pkg.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.SEARCH, 0)));
			send(pkg);
		}
	}
	
	public void connect(String addr) {
		if(serverSession == null) {
			SkyPackage pkg = new SkyPackage();
			pkg.addr = addr;
			pkg.port = ServerClient.PORT;
			pkg.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.CONNECT, 0)));
			send(pkg);
		}
	}
	
	public void disconnect() {
		if(serverSession != null) {
			SkyPackage pkg = new SkyPackage();
			pkg.addr = serverSession.addr;
			pkg.port = ServerClient.PORT;
			pkg.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.DISCONNECT, 0)));
			send(pkg);
		}
	}

	@Override
	public void onTimeout(Session s) {
		// TODO Auto-generated method stub
		mISkyClient.onServerTimeout(s);
	}

	@Override
	public Session onHandlePackageSession(SkyPackage pkg) {
		// TODO Auto-generated method stub
		if(serverSession.equals(pkg.name, pkg.addr))
			return serverSession;
		return null;
	}

	@Override
	public Session getServer() {
		// TODO Auto-generated method stub
		return serverSession;
	}

	@Override
	public void onNewTCPSession(TCPServer server, Socket s) {
		// TODO Auto-generated method stub
		try {
			serverSession.addTCPSession(new TCPSession(s, server));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTCPSessionClosed(TCPSession s) {
		// TODO Auto-generated method stub
		serverSession.removeTCPSession(s);
	}
}
