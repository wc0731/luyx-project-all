package com.skyworth.skyplay.framework;

import java.util.ArrayList;

public class Service {
	public interface IServiceServer {
		Session onHandlePackageSession(SkyPackage pkg);
		ArrayList<Session> getClientList();
	}
	
	public interface IServiceClient {
		Session onHandlePackageSession(SkyPackage pkg);
		Session getServer();
	}
}
