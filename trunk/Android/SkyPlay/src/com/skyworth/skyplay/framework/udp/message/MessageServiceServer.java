package com.skyworth.skyplay.framework.udp.message;

import java.net.SocketException;
import java.util.ArrayList;

import com.skyworth.skyplay.framework.Service.IServiceServer;
import com.skyworth.skyplay.framework.Session;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.UDP;
import com.skyworth.skyplay.framework.udp.message.MessageService.MessageServicePackage;

public class MessageServiceServer extends UDP {
	public interface IMessageServiceServer {
		public void onReceivePackage(Session c, MessageServicePackage pkg);
	}

	protected IMessageServiceServer mIMessageServiceServer = null;
	
	private IServiceServer mIServiceServer = null;
	
	public MessageServiceServer(IMessageServiceServer i, IServiceServer iss) throws SocketException {
		super(MessageService.PORT);
		// TODO Auto-generated constructor stub
		mIMessageServiceServer = i;
		mIServiceServer = iss;
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
		Session s = mIServiceServer.onHandlePackageSession(pkg);
		if(s != null)
			mIMessageServiceServer.onReceivePackage(s, MessageServicePackage.toPackage(pkg.data));
	}
	
	public void broadcast(String msg) {
		ArrayList<Session> list = mIServiceServer.getClientList();
		for(int i = 0; i < list.size(); i++) {
			SkyPackage pkg = new SkyPackage(list.get(i).addr, MessageService.PORT, MessageServicePackage.toBytes(new MessageServicePackage(msg)));
			send(pkg);
		}
	}
	
	public void sendToClient(String msg, Session c) {
		SkyPackage pkg = new SkyPackage(c.addr, MessageService.PORT, MessageServicePackage.toBytes(new MessageServicePackage(msg)));
		send(pkg);
	}
}
