package com.skyworth.skyplay.framework;

public class Connection {
	public String name = null;
	public String addr = null;
	public int timeout = 0;
	
	public Connection(String n, String a) {
		name = n;
		addr = a;
	}
	
	public boolean isConnectionPackage(SkyPackage pkg) {
		if(name.equals(pkg.name) && addr.equals(pkg.addr))
			return true;
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Connection client = (Connection)obj;
		if((name.compareToIgnoreCase(client.name) == 0) && (addr.compareToIgnoreCase(client.addr) == 0))
			return true;
		return false;
	}
}
