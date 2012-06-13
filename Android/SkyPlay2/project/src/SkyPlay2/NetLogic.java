package SkyPlay2;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;

public abstract class NetLogic {
 
	private LinkedList<SkyPackages> mSendPackagesList = new LinkedList<SkyPackages>();
	 
	private LinkedList<SkyPackages> mReceivePackagesList = new LinkedList<SkyPackages>();
	
	protected NetLogic() {
	}
	
	public void start() {
		if(!sendThread.isAlive())
			sendThread.start();
		if(!recvThread.isAlive())
			recvThread.start();
		if(!handleThread.isAlive())
			handleThread.start();
	}
	 
	public void sendPackages(SkyPackages pkg) {
		mSendPackagesList.add(pkg);
	}
	 
	private Thread sendThread = new Thread() {
		public void run() {
			try {
				while(true) {
					while(NetLogic.this.isAlive()) {
						if(mSendPackagesList.size() > 0)
							onSendPackages(mSendPackagesList.poll());
						else
							Thread.sleep(100);
					}
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	 
	private Thread recvThread = new Thread() {
		public void run() {
			while(true) {
				while(NetLogic.this.isAlive()) {
					SkyPackages pkg = onReceivePackages();
					if(pkg != null)
						mReceivePackagesList.add(pkg);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	 
	private Thread handleThread = new Thread() {
		public void run() {
			try {
				while(true) {
					while(NetLogic.this.isAlive()) {
						if(mReceivePackagesList.size() > 0)
							onHandlePackages(mReceivePackagesList.poll());
						else
							Thread.sleep(100);
					}
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	 
	protected abstract boolean isAlive();
	protected abstract void onSendPackages(SkyPackages pkg);
	protected abstract SkyPackages onReceivePackages();
	protected abstract void onHandlePackages(SkyPackages pkg);
	
	private static String localip = null;
	protected static String getLocalIP() throws SocketException {
		if(localip == null) {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
				NetworkInterface intf = en.nextElement();  
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
					InetAddress inetAddress = enumIpAddr.nextElement();  
					if (!inetAddress.isLoopbackAddress())
						localip = inetAddress.getHostAddress().toString();  
				}
			}
		}
		return localip;
	}
}
 
