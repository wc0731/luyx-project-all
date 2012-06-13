package skyworth.skyplay2.client;

import java.util.ArrayList;

import SkyPlay2.Session;
import SkyPlay2.Client.CServer;
import SkyPlay2.Client.Client;
import SkyPlay2.Client.Client.IClient;
import SkyPlay2.Services.MessageService;
import SkyPlay2.Services.MessageService.IMessageService;
import SkyPlay2.Services.MessageService.MessageServiceClient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SkyPlay2ClientActivity extends Activity implements IClient, IMessageService {
	private Client mClient = null;
	
	private MessageServiceClient mMessageServiceClient = null;
	
	Button Search, Connect, Disconnect, btnSend;
	TextView txtLog;
	EditText editText;
	CServer server;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Search = (Button)findViewById(R.id.Search);
        Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				txtLog.setText("");
				mClient.search();
			}
        });
        
        Connect = (Button)findViewById(R.id.Connect);
        Connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mClient.connect(server);
			}
        });
        
        Disconnect = (Button)findViewById(R.id.Disconnect);
        Disconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mClient.disconnect();
			}
        });
        
        btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mMessageServiceClient.sendMessage(mClient, editText.getText().toString());
				editText.setText("");
			}
        });
        editText = (EditText)findViewById(R.id.editText1);
        txtLog = (TextView)findViewById(R.id.txtLog);
        
        mMessageServiceClient = (MessageServiceClient) MessageService.getClientInstance();
        mMessageServiceClient.setMessageServiceListener(this);
        
        mClient = Client.getInstance();
        mClient.setClientListener(this);
        
        mClient.registerService(mMessageServiceClient);
    }
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mClient.destroy();
		super.onDestroy();
	}

	@Override
	public void onSearchFinished(ArrayList<CServer> list) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = list;
		onSearchFinishedHandler.sendMessage(msg);
	}
	
	private Handler onSearchFinishedHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			ArrayList<CServer> list = (ArrayList<CServer>)msg.obj;
			for(int i = 0; i < list.size(); i++) 
				txtLog.setText(txtLog.getText() + "find server:" + list.get(i).addr + "(" + list.get(i).name + ")\n");
			if(list.size() > 0)
				server = list.get(0);
			else
				txtLog.setText(txtLog.getText() + "no server found!!\n");
		}
	};

	@Override
	public void onConnectFinished(boolean r) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = Boolean.valueOf(r);
		onConnectFinishedHandler.sendMessage(msg);
	}
	
	private Handler onConnectFinishedHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Boolean r = (Boolean)msg.obj;
			if(r.booleanValue())
				txtLog.setText(txtLog.getText() + "server connect successfully!\n");
			else
				txtLog.setText(txtLog.getText() + "server connect failed!\n");
		}
	};

	@Override
	public void onDisconnectFinished(boolean r) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = r;
		onDisconnectFinishedHandler.sendMessage(msg);
	}
	
	private Handler onDisconnectFinishedHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Boolean r = (Boolean)msg.obj;
			if(r.booleanValue())
				txtLog.setText(txtLog.getText() + "server disconnect successfully!\n");
			else
				txtLog.setText(txtLog.getText() + "server disconnect failed!\n");
		}
	};

	class MessageClass {
		Session c;
		String msg;
	}
	
	@Override
	public void onReceiveMessage(Session client, String msg) {
		// TODO Auto-generated method stub
		MessageClass mc = new MessageClass();
		mc.c = client;
		mc.msg = msg;
		
		Message m = new Message();
		m.obj = mc;
		onReceiveMessageHandler.sendMessage(m);
	}
	
	private Handler onReceiveMessageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
	};
}