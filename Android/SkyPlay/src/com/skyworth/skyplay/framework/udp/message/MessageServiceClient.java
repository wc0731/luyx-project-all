package com.skyworth.skyplay.framework.udp.message;

import java.net.SocketException;

import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.UDP;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyClient;
import com.skyworth.skyplay.framework.udp.message.MessageService.MessageServicePackage;

public class MessageServiceClient extends UDP {
	public interface IMessageServiceClient {
		void onReceivePackage(MessageServicePackage pkg);
	}
	
	protected IMessageServiceClient mIMessageServiceClient = null;

	public MessageServiceClient(IMessageServiceClient i) throws SocketException {
		super(MessageService.PORT);
		// TODO Auto-generated constructor stub
		mIMessageServiceClient = i;
	}

	@Override
	protected void onSendFailed(SkyPackage pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSendSuccessfully(SkyPackage pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onHandlePackage(SkyPackage pkg) {
		// TODO Auto-generated method stub
		if(SkyClient.ServerConnection.equals(SkyPackage.getConnection(pkg))) {
			MessageServicePackage p = MessageServicePackage.toPackage(pkg.data);
			mIMessageServiceClient.onReceivePackage(p);
		}
	}
	
	public SkyPackage sendMessage(String msg) {
		SkyPackage pkg = new SkyPackage(SkyClient.ServerConnection.addr, MessageService.PORT, MessageServicePackage.toBytes(new MessageServicePackage(msg)));
		send(pkg);
		return pkg;
	}
}
