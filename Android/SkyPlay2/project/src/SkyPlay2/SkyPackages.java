package SkyPlay2;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class SkyPackages extends Packages implements Serializable {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -7231974312810508724L;

	private transient static final int DATALEN = 8192;
	 
	public byte[] data = new byte[DATALEN];
	 
	public transient String addr = "", name = "";
	 
	public int port;
	 
	public int service;
	
	 
	public static DatagramPacket toDatagramPacket(SkyPackages pkg) {
		try {
			byte[] d = SkyPackages.toBytes(pkg);
			return new DatagramPacket(d, d.length, InetAddress.getByName(pkg.addr), pkg.port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	 
	public void addData(byte[] d) {
		for(int i = 0; i < d.length; i++)
			data[i] = d[i];
	}

	private static int size = -1;
	public static int size() {
		// TODO Auto-generated method stub
		if(size == -1)
			size = SkyPackages.toBytes(new SkyPackages()).length;
		System.out.println("SkyPackages size:" + size);
		return size;
	}
}
