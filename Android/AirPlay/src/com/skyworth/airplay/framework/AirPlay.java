package com.skyworth.airplay.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public abstract class AirPlay extends Activity {
	protected static class FileInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4500907994030059397L;
		
		public transient DataOutputStream fileos = null;
		public transient int sign = 0;
		
		public String filname = null;
		public long size = 0;
		public int index = 0;
		
		public static FileInfo toFileInfo(byte[] d) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(d);
				ObjectInputStream oin = new ObjectInputStream(bin);
				FileInfo pkg = (FileInfo)oin.readObject();
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
		
		public static byte[] toBytes(FileInfo pkg) {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();   
				ObjectOutputStream oout = new ObjectOutputStream(bout);
				oout.writeObject(pkg);     
				oout.close();
				return bout.toByteArray();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null; 
		}
	}
	
	protected enum Command {
		HEARTBEAT,
		
		CLIENT_BROADCAST_SEARCH,
		CLIENT_REQUEST_CONNECT,
		CLIENT_REQUEST_DISCONNECT,
		
		CLIENTINFO,
		
		SERVER_SEARCH_RESPONSE,
		SERVER_CONNECT_RESPONSE,
		SERVER_DISCONNECT_RESPONSE,
		
		TRANSMIT,
		UPIMAGE,
		SHARE,
		DOWNIMAGE,
		SENDING,
		SENDINGEND,
	}
	

	public static class SenderInfo {
		public String addr = "";
		public int port = 0;
		
		public SenderInfo(String a, int p) {
			addr = a;
			port = p;
		}
		
		public String getID() {
			return addr+port;
		}
		
		public static String caluID(String a, int p) {
			return a+p;
		}
	}
	
	protected static final int SERVER_PORT = 12302;
	protected static final int CLIENT_PORT = 12402;
	
	private LinkedList<DatagramPacket> recvPkgList = new LinkedList<DatagramPacket>();
	private LinkedList<DatagramPacket> sendPkgList = new LinkedList<DatagramPacket>();
	
	private DatagramSocket mDatagramSocket = null;
	public String SharePath = "/sdcard/airplay/";

	protected ArrayList<FileInfo> recvinglist = new ArrayList<FileInfo>();
	
	public abstract String getTargetAddress();
	public abstract int getTargetPort();
	
	private IAirPlay mIAirPlay = null;
	
	public void initAirPlay(int port) throws SocketException {
		mDatagramSocket = new DatagramSocket(port);
		ReceivePackageThread recvT = new ReceivePackageThread();
		recvT.start();
		SendPackageThread sendT = new SendPackageThread();
		sendT.start();
		HandlePackageThread handleT = new HandlePackageThread();
		handleT.start();
	}
	
	public void setIAirPlay(IAirPlay i) {
		mIAirPlay = i;
	}
	
	protected void sendPackage(AirPackage pkg) {
		sendPkgList.add(AirPackage.toDatagramPacket(pkg));
	}
	
	protected void sendHeart(String addr, int port) {
		AirPackage pkg = new AirPackage();
		pkg.addr = addr;
		pkg.port = port;
		pkg.service = 0;
		pkg.cmd = Command.HEARTBEAT.ordinal();
		sendPackage(pkg);
	}
	
	protected int createStartPackage(Command cmd, String f, int index) {
		AirPackage pkg = new AirPackage();
		File file = new File(f);
		FileInfo fi = new FileInfo();
		fi.filname = file.getName();
		fi.size = file.length();
		fi.index = index;
		pkg.addr = getTargetAddress();
		pkg.port = getTargetPort();
		pkg.service = 0;
		pkg.cmd = cmd.ordinal();
		pkg.sign = (int) (Math.random()*999999);
		pkg.setData(FileInfo.toBytes(fi));
		sendPackage(pkg);
		return pkg.sign;
	}
	
	protected void createFilPackage(String f, int sign) {
		try {
			DataInputStream readFile = new DataInputStream(new FileInputStream(f));
			while(true) {
				AirPackage pkg = new AirPackage();
				pkg.addr = getTargetAddress();
				pkg.port = getTargetPort();
				pkg.service = 0;
				pkg.cmd = Command.SENDING.ordinal();
				pkg.sign = sign;
				pkg.len = readFile.read(pkg.data);
				if(pkg.len == -1)
					break;
				sendPackage(pkg);
			}
			readFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void createEndPackage(int sign, int index) {
		AirPackage pkg = new AirPackage();
		pkg.addr = getTargetAddress();
		pkg.port = getTargetPort();
		pkg.service = 0;
		pkg.cmd = Command.SENDINGEND.ordinal();
		pkg.sign = sign;
		pkg.len = index;
		sendPackage(pkg);
	}
	
	protected void handle(DatagramPacket pkg) {
		try {
			FileInfo fi = null;
			byte[] dd = null;
			AirPackage pp = AirPackage.toPackage(pkg.getData());
			if(pp.service == 0) {
				SenderInfo sender = new SenderInfo(pkg.getAddress().getHostAddress(), pkg.getPort());
				Command cmd = Command.values()[pp.cmd];
				switch(cmd) {
					case TRANSMIT:
						startReveivefile(sender, pp);
						break;
					case UPIMAGE:
						startReveivefile(sender, pp);
						mIAirPlay.getUPPackage(sender);
						break;
					case DOWNIMAGE:
						startReveivefile(sender, pp);
						mIAirPlay.getDOWNPackage(sender);
						break;
					case SHARE:
						startReveivefile(sender, pp);
						mIAirPlay.getSharePackage(sender);
						break;
					case SENDING:
						for(int i = 0; i < recvinglist.size(); i++) {
							if(recvinglist.get(i).sign == pp.sign) {
								fi = recvinglist.get(i);
								break;
							}
						}
						if(fi != null) {
							dd = new byte[pp.len];
							for(int i = 0; i < pp.len; i++)
								dd[i] = pp.data[i];
							fi.fileos.write(dd);
							fi.fileos.flush();
						}
						break;
					case SENDINGEND:
						for(int i = 0; i < recvinglist.size(); i++) {
							if(recvinglist.get(i).sign == pp.sign) {
								fi = recvinglist.get(i);
								break;
							}
						}
						if(fi != null) {
							fi.fileos.close();
							mIAirPlay.fileReceived(sender, SharePath + fi.filname, pp.len);
							recvinglist.remove(fi);
						}
						break;
					case HEARTBEAT:
						System.out.println("HEARTBEAT!!");
						mIAirPlay.heartbeat(sender);
						break;
					default:
						break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean startReveivefile(SenderInfo sender, AirPackage pp) {
		try {
			byte[] dd = new byte[pp.len];
			for(int i = 0; i < pp.len; i++)
				dd[i] = pp.data[i];
			FileInfo fi = FileInfo.toFileInfo(dd);
			String file = SharePath + fi.filname;
			File f = new File(file);
			if(f.exists()) {
				mIAirPlay.fileReceived(sender, file, fi.index);
				return false;
			}
			fi.sign = pp.sign;
			fi.fileos = new DataOutputStream(new FileOutputStream(file));
			recvinglist.add(fi);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	protected void setSharePath(String path) {
		SharePath = path;
	}
	
	private class ReceivePackageThread extends Thread {
		public void run() {
	        try {
				while(true) {
					byte[] recvBuf = new byte[AirPackage.size];
			        DatagramPacket recvPacket = new DatagramPacket(recvBuf, AirPackage.size);
					mDatagramSocket.receive(recvPacket);
					synchronized(recvPkgList) {
						recvPkgList.add(recvPacket);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class SendPackageThread extends Thread {
		public void run() {
			boolean t = false;
			while(true) {
				try {
					synchronized(recvPkgList) {
						if(sendPkgList.size() > 0) {
							mDatagramSocket.send(sendPkgList.poll());
							t = false;
						}
						else 
							t = true;
					}
					if(t)
						Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private class HandlePackageThread extends Thread {
		public void run() {
			boolean t = false;
			try {
				while(true) {
					synchronized(recvPkgList) {
						if(recvPkgList.size() > 0) {
							Message msg = new Message();
							msg.obj = recvPkgList.poll();
							HandlePackageThreadHandler.sendMessage(msg);
							t = false;
						} 
						else
							t = true;
					}
					if(t)
						Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Handler HandlePackageThreadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			DatagramPacket p = (DatagramPacket)msg.obj;
			handle(p);
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mDatagramSocket.close();
		super.onDestroy();
	}
}
