package com.skyworth.skyplay.framework;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Util {
	private static String localip = null;
	public static String getLocalIP() throws SocketException {
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
	
	private static boolean EN_LOG = true;
	public static void logger(String log) {
		if(EN_LOG)
			System.out.println(log);
	}
}
