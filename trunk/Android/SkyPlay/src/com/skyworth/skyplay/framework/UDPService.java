package com.skyworth.skyplay.framework;

import java.net.SocketException;

public abstract class UDPService extends UDP {

	protected IService mIService = null;
	
	public UDPService(IService is, int port) throws SocketException {
		super(port);
		// TODO Auto-generated constructor stub
		mIService = is;
	}

	@Override
	protected void onHandlePackage(SkyPackage pkg) {
		// TODO Auto-generated method stub
		
	}
}
