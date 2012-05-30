package com.skyworth.skyplayclient;

import java.net.SocketException;

import com.skyworth.skyplay.framework.Connection;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyClient;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyClient.ISkyClient;
import com.skyworth.skyplay.framework.udp.message.MessageService.MessageServicePackage;
import com.skyworth.skyplay.framework.udp.message.MessageServiceClient;
import com.skyworth.skyplay.framework.udp.message.MessageServiceClient.IMessageServiceClient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SkyPlayClientActivity extends Activity implements ISkyClient, IMessageServiceClient {
	SkyClient mSkyClient = null;
	Button Search = null;
	Button Connect = null;
	Button Disconnect = null;
	TextView txtInfo = null;
	TextView txtHeartBeat = null;
	Connection server = null;
	
	
	private EditText sendText = null;
	private Button btnSend = null;
	private TextView txtShowMsg = null;
	private MessageServiceClient mMessageServiceClient = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        try {
			mSkyClient = new SkyClient(this);
			mMessageServiceClient = new MessageServiceClient(this);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Search = (Button)this.findViewById(R.id.Search);
        Search.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			// TODO Auto-generated method stub
    			mSkyClient.search();
    		}
    	});
        
        Connect = (Button)this.findViewById(R.id.Connect);
        Connect.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			// TODO Auto-generated method stub
    			mSkyClient.connect(server);
    		}
    	});
        
        Disconnect = (Button)this.findViewById(R.id.Disconnect);
        Disconnect.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			// TODO Auto-generated method stub
    			mSkyClient.disconnect();
    		}
    	});
        
        txtInfo = (TextView)this.findViewById(R.id.txtInfo);
        txtHeartBeat = (TextView)this.findViewById(R.id.txtHeartBeat);
        
        txtShowMsg = (TextView)findViewById(R.id.txtShowText);
        sendText = (EditText)findViewById(R.id.sendText);
        btnSend = (Button)findViewById(R.id.btnSendMsg);
        btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mMessageServiceClient.sendMessage(sendText.getText().toString());
			}
        });
    }

	@Override
	public void onSearchResponse(Connection c) {
		// TODO Auto-generated method stub
		server = c;
		onSearchResponseHandler.sendEmptyMessage(0);
	}
	
	private Handler onSearchResponseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			txtInfo.setText("search server found:" + server.name + ":" + server.addr);
		}
	};

	@Override
	public void onSearchTimeout() {
		// TODO Auto-generated method stub
		onSearchTimeoutHandler.sendEmptyMessage(0);
	}
	
	private Handler onSearchTimeoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			txtInfo.setText("search server timeout!!");
		}
	};

	@Override
	public void onConnectResponse(Connection c) {
		// TODO Auto-generated method stub
		onConnectResponseHandler.sendEmptyMessage(0);
	}
	
	private Handler onConnectResponseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			txtInfo.setText("server connected:" + server.name + ":" + server.addr);
		}
	};

	@Override
	public void onConnectTimeout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnetResponse(Connection c) {
		// TODO Auto-generated method stub
		onDisconnetResponseHandler.sendEmptyMessage(0);
	}
	
	private Handler onDisconnetResponseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			txtInfo.setText("server disconnected:" + server.name + ":" + server.addr);
		}
	};

	@Override
	public void onDisconnectTimeout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionTimeout(Connection c) {
		// TODO Auto-generated method stub
		onConnectionTimeoutHandler.sendEmptyMessage(0);
	}
	
	private Handler onConnectionTimeoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			txtHeartBeat.setText("onConnectionTimeout!!!");
		}
	};

	@Override
	public void onHeartBeat(Connection c) {
		// TODO Auto-generated method stub
		onHeartBeatHandler.sendEmptyMessage(0);
	}
	
	int hb = 0;
	
	private Handler onHeartBeatHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			txtHeartBeat.setText("HeartBeat:" + hb++);
		}
	};

	@Override
	public void onReceivePackage(MessageServicePackage pkg) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = pkg;
		onReceivePackageHandler.sendMessage(msg);
	}
	
	private Handler onReceivePackageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			MessageServicePackage pkg = (MessageServicePackage)msg.obj;
			txtShowMsg.setText(txtShowMsg.getText() + pkg.data + "\n");
		}
	};
}