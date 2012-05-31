package com.skyworth.skyplayserver;

import java.net.SocketException;
import java.util.ArrayList;

import com.skyworth.skyplay.framework.Session;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer.ISkyServer;
import com.skyworth.skyplay.framework.udp.message.MessageService.MessageServicePackage;
import com.skyworth.skyplay.framework.udp.message.MessageServiceServer;
import com.skyworth.skyplay.framework.udp.message.MessageServiceServer.IMessageServiceServer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SkyPlayServerActivity extends Activity implements ISkyServer, IMessageServiceServer {
	private SkyServer mSkyServer = null;
	private LinearLayout linearClientInfo = null;
	private TextView txtShowMsg = null;
	private MessageServiceServer mMessageServiceServer = null;
	
	class ClientInfo {
		public Session session = null;
		public TextView txt = null;
		
		public ClientInfo(Session s) {
			session = s;
			txt = new TextView(SkyPlayServerActivity.this);
		}
	}
	
	private ArrayList<ClientInfo> clientinfoList = new ArrayList<ClientInfo>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        txtShowMsg = (TextView)findViewById(R.id.txtShowMsg);
        linearClientInfo = (LinearLayout)findViewById(R.id.linearClientInfo);
        
        try {
			mSkyServer = new SkyServer(this);
			mMessageServiceServer = new MessageServiceServer(this, mSkyServer);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMessageServiceServer.onDestroy();
		mSkyServer.onDestroy();
		super.onDestroy();
	}
	
	private Handler onConnectionTimeoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			ClientInfo ci = findClientInfo((Session)msg.obj);
			if(ci != null) {
				clientinfoList.remove(ci);
				drawClientList();
			}
		}
	};
	
	private Handler onConnectHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Session c = (Session)msg.obj;
			ClientInfo ci = new ClientInfo(c);
			clientinfoList.add(ci);
			ci.txt.setText(ci.session.name + " connected!!");
			drawClientList();
		}
	};
	
	private Handler onDisconnectHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			ClientInfo ci = findClientInfo((Session)msg.obj);
			if(ci != null) {
				clientinfoList.remove(ci);
				drawClientList();
			}
		}
	};
	
	private class MSG {
		public Session c;
		public MessageServicePackage pkg;
		
		public MSG(Session cc, MessageServicePackage pp) {
			c = cc;
			pkg = pp;
		}
	}
	
	private Handler onReceivePackageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			MSG m = (MSG)msg.obj;
			
			txtShowMsg.setText(txtShowMsg.getText() + m.pkg.data + "---" + m.c.name + "\n");
			mMessageServiceServer.broadcast(m.pkg.data);
		}
	};

	@Override
	public void onReceivePackage(Session c, MessageServicePackage pkg) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = new MSG(c, pkg);
		onReceivePackageHandler.sendMessage(msg);
	}

	@Override
	public void onConnect(Session c) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = c;
		onConnectHandler.sendMessage(msg);
	}

	@Override
	public void onDisconnect(Session c) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = c;
		onDisconnectHandler.sendMessage(msg);
	}

	@Override
	public void onHeartBeat(Session c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int chkConnection(String name, String addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onClientTimeout(Session s) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = s;
		onConnectionTimeoutHandler.sendMessage(msg);
	}

	@Override
	public void onSessionConnectionChanged(ArrayList<Session> list) {
		// TODO Auto-generated method stub
		
	}
	
	private ClientInfo findClientInfo(Session s) {
		for(int i = 0; i < clientinfoList.size(); i++) {
			if(clientinfoList.get(i).session == s)
				return clientinfoList.get(i);
		}
		return null;
	}
	
	private void drawClientList() {
		linearClientInfo.removeAllViews();
		for(int i = 0; i < clientinfoList.size(); i++) 
			linearClientInfo.addView(clientinfoList.get(i).txt);
	}
}