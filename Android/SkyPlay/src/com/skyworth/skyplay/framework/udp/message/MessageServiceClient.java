package com.skyworth.skyplay.framework.udp.message;

import java.net.SocketException;

import com.skyworth.skyplay.framework.Service.IServiceClient;
import com.skyworth.skyplay.framework.Session;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.UDP;
import com.skyworth.skyplay.framework.udp.message.MessageService.MessageServicePackage;

public class MessageServiceClient extends UDP {
	public interface IMessageServiceClient {
		void onReceivePackage(MessageServicePackage pkg);
	}
	
	protected IMessageServiceClient mIMessageServiceClient = null;
	
	private IServiceClient mIServiceClient = null;

	public MessageServiceClient(IMessageServiceClient i, IServiceClient isc) throws SocketException {
		super(MessageService.PORT);
		// TODO Auto-generated constructor stub
		mIMessageServiceClient = i;
		mIServiceClient = isc;
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
		if(mIServiceClient.onHandlePackageSession(pkg) != null)
			mIMessageServiceClient.onReceivePackage(MessageServicePackage.toPackage(pkg.data));
	}
	
	public SkyPackage sendMessage(String msg) {
		Session s = mIServiceClient.getServer();
		if(s != null) {
			SkyPackage pkg = new SkyPackage(s.addr, MessageService.PORT, MessageServicePackage.toBytes(new MessageServicePackage(msg)));
			send(pkg);
			return pkg;
		}
		return null;
	}
}
