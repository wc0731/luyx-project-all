package SkyPlay2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import SkyPlay2.Server.ClientManager;

public class Session extends NetLogic {
	public interface ISession {
		public abstract void onHandlePackages(Session session, SkyPackages pkg);
		public abstract void onDisconnect(Session session);
	}
	
	protected Socket mSocket = null;
	 
	protected ObjectInputStream mObjectInputStream = null;
	 
	protected ObjectOutputStream mObjectOutputStream = null;
	 
	protected String mUID = null;
	
	protected ISession mISession = null;
	 
	public Session() {
	}
	
	public void setSessionListener(ISession i) {
		mISession = i;
	}
	 
	public Session(Socket socket, String uid) {
		try {
			mUID = uid;
			mSocket = socket;
			mObjectInputStream = new ObjectInputStream(mSocket.getInputStream());
			mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
			//start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public String getUID() {
		if(mUID == null && mSocket != null)
			mUID = ClientManager.getUID(mSocket);
		return mUID;
	}
	
	public void disconnect() {
		try {
			if(mSocket != null) {
				mSocket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected boolean isAlive() {
		// TODO Auto-generated method stub
		if(mSocket != null) {
			if(!mSocket.isClosed() && mSocket.isConnected()) {
				try{
					mSocket.sendUrgentData(0xFF);
				}catch(Exception ex){
					mISession.onDisconnect(this);
					mSocket = null;
					return false;
				}
				return true;
			}
			mISession.onDisconnect(this);
			mSocket = null;
		}
		return false;
	}

	@Override
	protected void onSendPackages(SkyPackages pkg) {
		// TODO Auto-generated method stub
		try {
			System.out.println("onSendPackages!!");
			mObjectOutputStream.writeObject(pkg);
			mObjectOutputStream.flush();
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
			return (SkyPackages)mObjectInputStream.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onHandlePackages(SkyPackages pkg) {
		// TODO Auto-generated method stub
		System.out.println("onHandlePackages!!");
		mISession.onHandlePackages(this, pkg);
	}
}
 
