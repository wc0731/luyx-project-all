package com.skyworth.skyplayserver;

import java.net.SocketException;

import com.skyworth.skyplay.framework.Connection;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer.ISkyServer;
import com.skyworth.skyplay.framework.udp.message.MessageService.MessageServicePackage;
import com.skyworth.skyplay.framework.udp.message.MessageServiceServer;
import com.skyworth.skyplay.framework.udp.message.MessageServiceServer.IMessageServiceServer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class SkyPlayServerActivity extends Activity implements ISkyServer, IMessageServiceServer {
	private SkyServer mSkyServer = null;
	
	private TextView txtClientInfo = null;
	private TextView txtHeartBeat = null;
	
	private TextView txtShowMsg = null;
	
	private MessageServiceServer mMessageServiceServer = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        txtClientInfo = (TextView)findViewById(R.id.txtClientInfo);
        txtHeartBeat = (TextView)findViewById(R.id.txtHeartBeat);
        txtShowMsg = (TextView)findViewById(R.id.txtShowMsg);
        
        try {
			mSkyServer = new SkyServer(this);
			mMessageServiceServer = new MessageServiceServer(this);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void onConnectionTimeout(Connection c) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = c;
		onConnectionTimeoutHandler.sendMessage(msg);
	}
	
	private Handler onConnectionTimeoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Connection c = (Connection)msg.obj;
			txtClientInfo.setText("client:" + c.addr + ":" + c.name + " timeout!!");
		}
	};

	@Override
	public void onConnect(Connection c) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = c;
		onConnectHandler.sendMessage(msg);
	}
	
	private Handler onConnectHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Connection c = (Connection)msg.obj;
			txtClientInfo.setText("client:" + c.addr + ":" + c.name + " onConnect!!");
		}
	};

	@Override
	public void onDisconnect(Connection c) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = c;
		onDisconnectHandler.sendMessage(msg);
	}
	
	private Handler onDisconnectHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Connection c = (Connection)msg.obj;
			txtClientInfo.setText("client:" + c.addr + ":" + c.name + " onDisconnect!!");
		}
	};

	@Override
	public void onHeartBeat(Connection c) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = c;
		onHeartBeatHandler.sendMessage(msg);
	}
	
	private int hb = 0;
	
	private Handler onHeartBeatHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			hb++;
			Connection c = (Connection)msg.obj;
			txtHeartBeat.setText("client:" + c.addr + ":" + c.name + " heartbeat:" + hb);
		}
	};
	
	private class MSG {
		public Connection c;
		public MessageServicePackage pkg;
		
		public MSG(Connection cc, MessageServicePackage pp) {
			c = cc;
			pkg = pp;
		}
	}

	@Override
	public void onReceivePackage(Connection c, MessageServicePackage pkg) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = new MSG(c, pkg);
		onReceivePackageHandler.sendMessage(msg);
	}
	
	private Handler onReceivePackageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			MSG m = (MSG)msg.obj;
			
			txtShowMsg.setText(txtShowMsg.getText() + m.pkg.data + "---" + m.c.name + "\n");
			mMessageServiceServer.sendToAllClient(m.pkg.data);
		}
	};
}