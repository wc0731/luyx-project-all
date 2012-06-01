package com.skyworth.skyplay.framework;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Session {
	public static class SessionPackage extends Packages implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 2412906758294426758L;
		public COMMAND cmd;
		public int info;

		public enum COMMAND {
			SEARCH,
			CONNECT,
			DISCONNECT,
			
			RES_SEARCH,
			RES_CONNECT,
			RES_DISCONNECT,
			
			HEARTBEAT,
		}	
		
		public SessionPackage(COMMAND c, int s) {
			cmd = c;
			info = s;
		}
	}
	
	private static final int TIMEOUT = 30;
	
	public interface ISession {
		void onTimeout(Session s);
	}
	
	public String name = null;
	public String addr = null;
	
	private Timer heatbeatTimer = null;
	
	private int timeout = 0;
	private ISession mISession = null;
	
	private ArrayList<TCPSession> tcpsessions = new ArrayList<TCPSession>();
	
	public Session(String n, String a, ISession is) {
		name = n;
		addr = a;
		mISession = is;
		
		heatbeatTimer = new Timer();
		heatbeatTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				timeout++;
				if(timeout >= TIMEOUT) {
					mISession.onTimeout(Session.this);
					heatbeatTimer.cancel();
				}
			}
        }, 0, 1000);
	}
	
	public void heartbeat() {
		timeout = 0;
	}
	
	public void close() throws IOException {
		for(int i = 0; i < tcpsessions.size(); i++)
			tcpsessions.get(i).close();
		tcpsessions.clear();
		heatbeatTimer.cancel();
	}
	
	public void addTCPSession(TCPSession s) {
		tcpsessions.add(s);
	}
	
	public void removeTCPSession(TCPSession s) {
		tcpsessions.remove(s);
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Session client = (Session)obj;
		return equals(client.name, client.addr);
	}
	
	public boolean equals(String n, String a) {
		if((name.compareToIgnoreCase(n) == 0) && (addr.compareToIgnoreCase(a) == 0))
			return true;
		return false;
	}
}
