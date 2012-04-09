package com.skyworth.airplay.phone;

import java.io.File;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.skyworth.airplay.framework.AirPackage;
import com.skyworth.airplay.framework.AirPlay;
import com.skyworth.airplay.framework.IAirPlay;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AirPlayPhone extends AirPlay implements IAirPlayPhone, IAirPlay {
	
	public static class ServerInfo extends SenderInfo {
		public boolean connect = false;
		
		public ServerInfo(String a, int p) {
			super(a, p);
			// TODO Auto-generated constructor stub
		}
	}
	
	public ArrayList<ServerInfo> srvlist = new ArrayList<ServerInfo>();
	public ServerInfo conServer = null;
	
	private IAirPlayPhone mIAirPlayPhone = null;

	private Timer heartTimer = new Timer();
	private int heartcount = 10;
	
	
	public String ReadPath = "/sdcard/airplay/";
	public ArrayList<String> filelist = null;
	public int curpoint = 0;
	
	
	private Button Search, Connect, Disconnect, PrevImage, Share, NextImage;
	private TextView txtServerInfo = null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mIAirPlayPhone = this;
        
        txtServerInfo = (TextView)findViewById(R.id.txtServerInfo);
        
        Search = (Button)findViewById(R.id.Search);
        Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				search();
			}
        });
        
        Connect = (Button)findViewById(R.id.Connect);
        Connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connect(srvlist.get(0));
			}
        });
        
        Disconnect = (Button)findViewById(R.id.Disconnect);
        Disconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				disconnect();
			}
        });
        
        PrevImage = (Button)findViewById(R.id.PrevImage);
        PrevImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIAirPlayPhone.cmd_doUP();
			}
        });
        
        Share = (Button)findViewById(R.id.Share);
        Share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIAirPlayPhone.cmd_share();
			}
        });
        
        NextImage = (Button)findViewById(R.id.NextImage);
        NextImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIAirPlayPhone.cmd_doDOWN();
			}
        });
        
        
        try {
			initAirPlay(CLIENT_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	protected void handle(DatagramPacket pkg) {
		// TODO Auto-generated method stub
		AirPackage pp = AirPackage.toPackage(pkg.getData());
		if(0 == pp.service) {
			Command cmd = Command.values()[pp.cmd];
			switch(cmd) {
				case SERVER_SEARCH_RESPONSE:
					System.out.println("SERVER_SEARCH_RESPONSE!!");
					if(newServer(pkg.getAddress().getHostAddress(), pkg.getPort()))
						mIAirPlayPhone.searchGetResult(srvlist);
					break;
				case SERVER_CONNECT_RESPONSE:
					System.out.println("SERVER_CONNECT_RESPONSE!!");
					mIAirPlayPhone.connectGetResult();
					break;
				case SERVER_DISCONNECT_RESPONSE:
					System.out.println("SERVER_DISCONNECT_RESPONSE!!");
					mIAirPlayPhone.disconnectGetResult();
					break;
				default:
					super.handle(pkg);
					break;
			}
		}
	}
	
	public void search() {
		AirPackage pkg = new AirPackage();
		pkg.addr = "255.255.255.255";
		pkg.port = SERVER_PORT;
		pkg.service = 0;
		pkg.cmd = Command.CLIENT_BROADCAST_SEARCH.ordinal();
		sendPackage(pkg);
	}
	
	public void connect(ServerInfo cs) {
		if(conServer == null) {
			conServer = cs;
			AirPackage pkg = new AirPackage();
			pkg.addr = conServer.addr;
			pkg.port = conServer.port;
			pkg.service = 0;
			pkg.cmd = Command.CLIENT_REQUEST_CONNECT.ordinal();
			sendPackage(pkg);
		}
	}
	
	public void disconnect() {
		if(conServer != null) {
			AirPackage pkg = new AirPackage();
			pkg.addr = conServer.addr;
			pkg.port = conServer.port;
			pkg.service = 0;
			pkg.cmd = Command.CLIENT_REQUEST_DISCONNECT.ordinal();
			sendPackage(pkg);
			conServer = null;
		}
	} 
	
	private boolean newServer(String ip, int port) {
		for(int i = 0; i < srvlist.size(); i++) {
			if(srvlist.get(i).getID().equals(ServerInfo.caluID(ip, port)))
				return false;
		}
		srvlist.add(new ServerInfo(ip, port));
		return true;
	}

	@Override
	public void searchGetResult(ArrayList<ServerInfo> l) {
		// TODO Auto-generated method stub
		txtServerInfo.setText(l.get(0).addr + ":" + l.get(0).port + "  found");
	}

	@Override
	public void connectGetResult() {
		// TODO Auto-generated method stub
		if(conServer != null) {
			conServer.connect = true;
			txtServerInfo.setText(conServer.addr + ":" + conServer.port + "  connected");
			mIAirPlayPhone.sendinfo();
			heartTimer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	            	if(heartcount == 0)
	            		mIAirPlayPhone.heartbeatTimeout();
	            	else 
	            		heartcount--;
	            }}, 0, 3000);
		}
	}

	@Override
	public void disconnectGetResult() {
		// TODO Auto-generated method stub
		if(conServer != null) {
			conServer.connect = false;
			txtServerInfo.setText(conServer.addr + ":" + conServer.port + "  disconnected");
			heartTimer.cancel();
			conServer = null;
		}
	}

	@Override
	public void heartbeat(SenderInfo sender) {
		// TODO Auto-generated method stub
		if(conServer.connect) {
			heartcount = 10;
			sendHeart(conServer.addr, conServer.port);
		}
	}

	@Override
	public void heartbeatTimeout() {
		// TODO Auto-generated method stub
		heartTimer.cancel();
	}

	@Override
	public void sendinfo() {
		// TODO Auto-generated method stub
		int l = readImage();
		
		AirPackage ap = new AirPackage();
		ap.addr = conServer.addr;
		ap.port = conServer.port;
		ap.service = 0;
		ap.cmd = Command.CLIENTINFO.ordinal();
		ap.len = l;
		sendPackage(ap);
		
		if(l > 6) {
			for(int i = 0; i < 7; i++)
				sendImage(Command.TRANSMIT, i);
		}
		else {
			for(int i = 0; i < l; i++)
				sendImage(Command.TRANSMIT, i);
		}
		curpoint = 3;
	}
	
	public void setReadPath(String path) {
		ReadPath = path;
	}
	
	public int readImage() {
		File file=new File(ReadPath);
        File[] files=file.listFiles();
        int filesAmount=files.length;
        ArrayList<String> ImgPathArray=new ArrayList<String>();
        System.out.println("create ImgBitmapArray");
        for(int count=0;count<filesAmount;count++){
        	if(files[count].getName().endsWith(".jpg")||(files[count].getName().endsWith(".JPG"))){
        		ImgPathArray.add(files[count].getAbsolutePath());
        	}else if(files[count].isDirectory()){
        		
        	}
        }
        filelist = ImgPathArray;
        return filelist.size();
	}
	
	private void sendImage(Command cmd) {
		int s = createStartPackage(cmd, filelist.get(curpoint), curpoint);
		createFilPackage(filelist.get(curpoint), s);
		createEndPackage(s, curpoint);
	}
	
	private void sendImage(Command cmd, int pos) {
		int s = createStartPackage(cmd, filelist.get(pos), pos);
		createFilPackage(filelist.get(pos), s);
		createEndPackage(s, pos);
	}

	@Override
	public String getTargetAddress() {
		// TODO Auto-generated method stub
		return conServer.addr;
	}

	@Override
	public int getTargetPort() {
		// TODO Auto-generated method stub
		return conServer.port;
	}

	@Override
	public void fileReceived(SenderInfo sender, String path, int index) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "getShareImage:" + path, 3000);
	}

	@Override
	public void getUPPackage(SenderInfo sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getDOWNPackage(SenderInfo sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getSharePackage(SenderInfo sender) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "getSharePackage", 3000);
	}

	@Override
	public void cmd_doUP() {
		// TODO Auto-generated method stub
		if(curpoint < filelist.size() - 1) {
			curpoint++;
			sendImage(Command.UPIMAGE);
		}
	}

	@Override
	public void cmd_share() {
		// TODO Auto-generated method stub
		sendImage(Command.SHARE);
	}

	@Override
	public void cmd_doDOWN() {
		// TODO Auto-generated method stub
		if(curpoint > 0) {
			curpoint--;
			sendImage(Command.DOWNIMAGE);
		}
	}
}