package SkyPlay2;

import java.util.HashMap;

public class ServiceManager {
 
	private HashMap<Integer, Service> mServiceList = null;
	 
	private static ServiceManager mServiceManager = null;
	 
	public static ServiceManager getInstance() {
		if(mServiceManager == null)
			mServiceManager = new ServiceManager();
		return mServiceManager;
	}
	
	ServiceManager() {
		mServiceList = new HashMap<Integer, Service>();
	}
	 
	public void registerService(Service srv) {
		mServiceList.put(srv.getTag(), srv);
	}
	 
	public void handPackages(Session client, SkyPackages pkg) {
		System.out.println("ServiceManager.handPackages:" + pkg.service);
		Service srv = mServiceList.get(pkg.service);
		System.out.println("ServiceManager.handPackages srv:" + srv);
		if(srv != null)
			srv.handlePackages(client, pkg);
	}
}
 
