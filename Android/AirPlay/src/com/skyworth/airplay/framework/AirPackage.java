package com.skyworth.airplay.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class AirPackage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	
	public transient static final int DATALEN = 8192;
	
	public transient String addr = "";
	public transient int port = 0;
	
	public transient static int size = AirPackage.toBytes(new AirPackage()).length;
	
	public int head = -123;
	
	public int service;
	public int cmd;
	public int sign;
	
	public int len;
	public byte[] data = new byte[DATALEN];
	
	public AirPackage() {}
	
	public AirPackage(byte[] d) {
		setData(d);
	}
	
	public void setData(byte[] d) {
		int i;
		len = d.length;
		for(i = 0; i < d.length; i++)
			data[i] = d[i];
	}
	
	public static AirPackage toPackage(byte[] d) {
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(d);
			ObjectInputStream oin = new   ObjectInputStream(bin);
			AirPackage pkg = (AirPackage)oin.readObject();
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
	
	public static byte[] toBytes(AirPackage pkg) {
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
	
	public static DatagramPacket toDatagramPacket(AirPackage pkg) {
		try {
			byte[] d = AirPackage.toBytes(pkg);
			return new DatagramPacket(d, d.length, InetAddress.getByName(pkg.addr), pkg.port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
