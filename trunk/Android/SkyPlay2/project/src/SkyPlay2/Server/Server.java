package SkyPlay2.Server;

import java.io.IOException;
import java.net.ServerSocket;

import SkyPlay2.Config;
import SkyPlay2.Service;
import SkyPlay2.ServiceManager;
import SkyPlay2.Session;
import SkyPlay2.Session.ISession;
import SkyPlay2.SkyPackages;
import SkyPlay2.Server.ClientManager.IClientManager;

public class Server extends ServerSocket implements ISession {

	private UDPServer mUDPServer = null;
	 
	private static ClientManager mClientManager = null;
	 
	private static ServiceManager mServiceManager = null;
	 
	private static Server mServer = null;
	 
	public static Server getInstance() {
		try {
			if(mServer == null)
				mServer = new Server();
			return mServer;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	 
	public Server() throws IOException {
		super(Config.SERVER_PORT);
		// TODO Auto-generated constructor stub
		mClientManager = ClientManager.getInstance();
		mServiceManager = ServiceManager.getInstance();
		mUDPServer = UDPServer.getInstance();
		listenerThread.start();
	}
	 
	public void setClientManagerListener(IClientManager l) {
		mClientManager.setClientManagerListener(l);
	}
	 
	public void registerService(Service srv) {
		mServiceManager.registerService(srv);
	}
	 
	public void destroy() {
	 
	}
	 
	private Thread listenerThread = new Thread() {
		public void run() {
			try {
				while(!Server.this.isClosed()) {
					Session s = mClientManager.addClient(Server.this.accept());
					if(s != null) {
						s.setSessionListener(Server.this);
						s.start();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onDisconnect(Session session) {
		// TODO Auto-generated method stub
		mClientManager.removeClient(session);
	}

	@Override
	public void onHandlePackages(Session session, SkyPackages pkg) {
		// TODO Auto-generated method stub
		mServiceManager.handPackages(session, pkg);
	}
}