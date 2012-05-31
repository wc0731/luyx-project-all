package com.skyworth.skyplay.framework.udp.ServerClient;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.skyworth.skyplay.framework.Service.IServiceServer;
import com.skyworth.skyplay.framework.Session;
import com.skyworth.skyplay.framework.Session.ISession;
import com.skyworth.skyplay.framework.Session.SessionPackage;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.UDP;
import com.skyworth.skyplay.framework.Util;


public class SkyServer extends UDP implements ISession, IServiceServer {
	public interface ISkyServer {
		void onConnect(Session c);
		void onDisconnect(Session c);
		void onHeartBeat(Session c);
		int chkConnection(String name, String addr);
		
		void onClientTimeout(Session s);
		
		void onSessionConnectionChanged(ArrayList<Session> list);
	}
	
	public static ArrayList<Session> sessionConnection = new ArrayList<Session>();
	private ISkyServer mISkyServer = null;
	
	public SkyServer(ISkyServer isc) throws SocketException {
		super(ServerClient.PORT);
		// TODO Auto-generated constructor stub
		mISkyServer = isc;
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i = 0; i < sessionConnection.size(); i++) {
					SkyPackage pkg = new SkyPackage();
					pkg.addr = sessionConnection.get(i).addr;
					pkg.port = ServerClient.PORT;
					pkg.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.HEARTBEAT, 0)));
					send(pkg);
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
		SessionPackage pp = SessionPackage.toPackage(pkg.data);
		SkyPackage pkgpp = new SkyPackage();
		
		Session session = searchSession(pkg.name, pkg.addr);
		
		switch(pp.cmd) {
			case SEARCH:
				if(session == null) {
					pkgpp.addr = pkg.addr;
					pkgpp.port = ServerClient.PORT;
					pkgpp.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.RES_SEARCH, 0)));
					send(pkgpp);
				}
				break;
			case CONNECT:
				if(session == null) {
					int ret = mISkyServer.chkConnection(pkg.name, pkg.addr);
					pkgpp.addr = pkg.addr;
					pkgpp.port = ServerClient.PORT;
					pkgpp.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.RES_CONNECT, ret)));
					send(pkgpp);
					if(ret == 0)
						connectSession(pkg.name, pkg.addr);
				}
				break;
			case DISCONNECT:
				if(session != null) {
					pkgpp.addr = pkg.addr;
					pkgpp.port = ServerClient.PORT;
					pkgpp.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.RES_DISCONNECT, 0)));
					send(pkgpp);
					disconnectSession(session);
				}
				break;
			case HEARTBEAT:
				if(session != null) {
					pkgpp.addr = session.addr;
					pkgpp.port = ServerClient.PORT;
					pkgpp.setData(SessionPackage.toBytes(new SessionPackage(SessionPackage.COMMAND.HEARTBEAT, 0)));
					send(pkgpp);
					heartbeatSession(session);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onTimeout(Session s) {
		// TODO Auto-generated method stub
		mISkyServer.onClientTimeout(s);
	}
	
	private Session searchSession(String name, String addr) {
		for(int i = 0; i < sessionConnection.size(); i++) {
			if(sessionConnection.get(i).equals(name, addr))
				return sessionConnection.get(i);
		}
		return null;
	}
	
	private void connectSession(String name, String addr) {
		Session session = new Session(name, addr, this);
		sessionConnection.add(session);
		mISkyServer.onConnect(session);
		mISkyServer.onSessionConnectionChanged(sessionConnection);
	}
	
	public void disconnectSession(Session session) {
		mISkyServer.onDisconnect(session);
		sessionConnection.remove(session);
		mISkyServer.onSessionConnectionChanged(sessionConnection);
	}
	
	private void heartbeatSession(Session session) {
		session.heartbeat();
		mISkyServer.onHeartBeat(session);
	}

	@Override
	public Session onHandlePackageSession(SkyPackage pkg) {
		// TODO Auto-generated method stub
		return searchSession(pkg.name, pkg.addr);
	}

	@Override
	public ArrayList<Session> getClientList() {
		// TODO Auto-generated method stub
		return sessionConnection;
	}
}
