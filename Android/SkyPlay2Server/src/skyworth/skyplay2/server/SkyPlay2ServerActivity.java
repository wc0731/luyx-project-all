package skyworth.skyplay2.server;

import SkyPlay2.Session;
import SkyPlay2.Server.ClientManager.IClientManager;
import SkyPlay2.Server.Server;
import SkyPlay2.Services.MessageService;
import SkyPlay2.Services.MessageService.IMessageService;
import SkyPlay2.Services.MessageService.MessageServiceServer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class SkyPlay2ServerActivity extends Activity implements IClientManager, IMessageService {
	private Server mServer = null;
	private MessageServiceServer mMessageServiceServer = null;
	
	TextView txtClient, txtMsg;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mMessageServiceServer = (MessageServiceServer) MessageService.getServerInstance();
        mMessageServiceServer.setMessageServiceListener(this);
        
        mServer = Server.getInstance();
        mServer.setClientManagerListener(this);
        mServer.registerService(mMessageServiceServer);
        
        
        txtClient = (TextView)findViewById(R.id.txtClient);
        txtMsg = (TextView)findViewById(R.id.txtMsg);
    }
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
        mServer.destroy();
		super.onDestroy();
	}

	@Override
	public void onClientConnect(Session client) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = client;
		onClientConnectHandler.sendMessage(msg);
	}
	
	private Handler onClientConnectHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Session client = (Session)msg.obj;
			txtClient.setText(txtClient.getText() + "\n" + client.getUID() + " connected!!");
		}
	};

	@Override
	public void onClientDisconnect(Session client) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = client;
		onClientDisconnectHandler.sendMessage(msg);
	}
	
	private Handler onClientDisconnectHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Session client = (Session)msg.obj;
			txtClient.setText(txtClient.getText() + "\n" + client.getUID() + " disconnected!!");
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
			MessageClass mc = (MessageClass)msg.obj;
			txtClient.setText(txtClient.getText() + "\n" + mc.c.getUID() + " get message:" + mc.msg);
		}
	};
}