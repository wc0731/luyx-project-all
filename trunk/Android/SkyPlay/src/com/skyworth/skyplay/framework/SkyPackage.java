package com.skyworth.skyplay.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class SkyPackage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	
	public transient static final int DATALEN = 8192;
	public transient static final int SIZE = SkyPackage.toBytes(new SkyPackage()).length;
	
	public transient String name = null;
	public transient String addr = null;
	public transient int port = 0;
	public transient int timeout = 0;
	
	public long sign;	
	public int len;
	public byte[] data = new byte[DATALEN];
	public boolean isReceipt = false;
	
	public SkyPackage() {
		sign = (long)(Math.random()*Math.pow(2, 63));
	}
	
	public SkyPackage(Session c, int p) {
		addr = c.addr;
		port = p;
		sign = (long)(Math.random()*Math.pow(2, 63));
	}
	
	public SkyPackage(String a, int p) {
		addr = a;
		port = p;
		sign = (long)(Math.random()*Math.pow(2, 63));
	}
	
	public SkyPackage(String a, int p, byte[] d) {
		addr = a;
		port = p;
		sign = (long)(Math.random()*Math.pow(2, 63));
		setData(d);
	}
	
	public void setData(byte[] d) {
		int i;
		len = d.length;
		for(i = 0; i < d.length; i++)
			data[i] = d[i];
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		SkyPackage pkg = (SkyPackage)obj;
		if(sign == pkg.sign)
			return true;
		return false;
	}

	public static SkyPackage toPackage(byte[] d) {
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(d);
			ObjectInputStream oin = new   ObjectInputStream(bin);
			SkyPackage pkg = (SkyPackage)oin.readObject();
			oin.close();
			return pkg;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	public static byte[] toBytes(SkyPackage pkg) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();   
			ObjectOutputStream oout = new ObjectOutputStream(bout);
			oout.writeObject(pkg);     
			oout.close();
			byte[] bb = bout.toByteArray();
			return bb;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
	}
	
	public static DatagramPacket toDatagramPacket(SkyPackage pkg) {
		try {
			byte[] d = SkyPackage.toBytes(pkg);
			return new DatagramPacket(d, d.length, InetAddress.getByName(pkg.addr), pkg.port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
