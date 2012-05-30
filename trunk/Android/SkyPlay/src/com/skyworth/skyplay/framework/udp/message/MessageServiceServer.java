package com.skyworth.skyplay.framework.udp.message;

import java.net.SocketException;

import com.skyworth.skyplay.framework.Connection;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.UDP;
import com.skyworth.skyplay.framework.udp.ServerClient.SkyServer;
import com.skyworth.skyplay.framework.udp.message.MessageService.MessageServicePackage;

public class MessageServiceServer extends UDP {
	public interface IMessageServiceServer {
		public void onReceivePackage(Connection c, MessageServicePackage pkg);
	}

	protected IMessageServiceServer mIMessageServiceServer = null;
	
	public MessageServiceServer(IMessageServiceServer i) throws SocketException {
		super(MessageService.PORT);
		// TODO Auto-generated constructor stub
		mIMessageServiceServer = i;
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
		for(int i = 0; i < SkyServer.ClientConnection.size(); i++) {
			if(SkyServer.ClientConnection.get(i).equals(SkyPackage.getConnection(pkg))) {
				MessageServicePackage p = MessageServicePackage.toPackage(pkg.data);
				mIMessageServiceServer.onReceivePackage(SkyServer.ClientConnection.get(i), p);
				break;
			}
		}
	}
	
	public void sendToAllClient(String msg) {
		for(int i = 0; i < SkyServer.ClientConnection.size(); i++) {
			SkyPackage pkg = new SkyPackage(SkyServer.ClientConnection.get(i).addr, MessageService.PORT, MessageServicePackage.toBytes(new MessageServicePackage(msg)));
			send(pkg);
		}
	}
	
	public void sendToClient(String msg, Connection c) {
		SkyPackage pkg = new SkyPackage(c.addr, MessageService.PORT, MessageServicePackage.toBytes(new MessageServicePackage(msg)));
		send(pkg);
	}
}
