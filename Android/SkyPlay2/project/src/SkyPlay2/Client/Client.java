package SkyPlay2.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import SkyPlay2.Config;
import SkyPlay2.Service;
import SkyPlay2.ServiceManager;
import SkyPlay2.Session;
import SkyPlay2.Session.ISession;
import SkyPlay2.SkyPackages;

public class Client extends Session implements ISession {
	public interface IClient {
		public abstract void onSearchFinished(ArrayList<CServer> list);
		public abstract void onConnectFinished(boolean r);
		public abstract void onDisconnectFinished(boolean r);
	}
 
	private CServer mCServer = null;
	 
	private ArrayList<CServer> mServerList = null;
	 
	private ServiceManager mServiceManager = null;
	 
	private static Client mClient = null;
	 
	private IClient mClientListener = null;
	 
	private UDPClient mUDPClient = null;
	
	private Timer mSearchTimer = null;
	 
	public static Client getInstance() {
		if(mClient == null)
			mClient = new Client();
		return mClient;
	}
	
	Client() {
		mServerList = new ArrayList<CServer>();
		mServiceManager = ServiceManager.getInstance();
		mUDPClient = UDPClient.getInstance();
		setSessionListener(this);
	}
	 
	public void search() {
		if(mCServer == null && mSearchTimer == null) {
			mServerList.clear();
			mSearchTimer = new Timer();
			mSearchTimer.schedule(new TimerTask() {
				int count = 0;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(count == 10) {
						mSearchTimer.cancel();
						mSearchTimer = null;
						mClientListener.onSearchFinished(mServerList);
						return;
					}
					count++;
					System.out.println("mUDPClient.search!!");
					mUDPClient.search(Client.this);
				}
			}, 0, 1000);
		}
	}
	 
	public void connect(CServer server) {
		try {
			if(mSocket == null) {
				mSocket = new Socket(server.addr, Config.SERVER_PORT);
				mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
				mObjectInputStream = new ObjectInputStream(mSocket.getInputStream());
				mCServer = server;
				mClientListener.onConnectFinished(true);
				start();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mClientListener.onConnectFinished(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mClientListener.onConnectFinished(false);
		}
	}  
	 
	@Override
	public void disconnect() {
		if(mSocket != null) {
			super.disconnect();
			mCServer = null;
		}
	}

	public void destroy() {
		disconnect();
	}
	 
	public void registerService(Service srv) {
		mServiceManager.registerService(srv);
	}
	 
	public void setClientListener(IClient l) {
		mClientListener = l;
	}
	 
	public void onUDPClientSearchResponse(CServer s) {
		for(int i = 0; i < mServerList.size(); i++) {
			if(mServerList.get(i).addr.equals(s.addr) && mServerList.get(i).name.equals(s.name))
				return;
		}
		mServerList.add(s);
	}
	
	public CServer getConnectServer() {
		return mCServer;
	}

	@Override
	public void onDisconnect(Session session) {
		// TODO Auto-generated method stub
		mClientListener.onDisconnectFinished(true);
	}

	@Override
	public void onHandlePackages(Session session, SkyPackages pkg) {
		// TODO Auto-generated method stub
		mServiceManager.handPackages(session, pkg);
	}
}
