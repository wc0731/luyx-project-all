package SkyPlay2.Services;

import java.io.Serializable;

import SkyPlay2.Packages;
import SkyPlay2.Service;
import SkyPlay2.Session;
import SkyPlay2.SkyPackages;

public class MessageService {
	public static Service getServerInstance() {
		return new MessageServiceServer();
	}
	
	public static Service getClientInstance() {
		return new MessageServiceClient();
	}
	
	public static SkyPackages createPackages(String msg) {
		MessagePackages msgpkg = new MessagePackages(msg);
		SkyPackages pkg = new SkyPackages();
		pkg.service = TAG;
		pkg.addData(MessagePackages.toBytes(msgpkg));
		return pkg;
	}
	
	public static class MessagePackages extends Packages implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7796272552948743674L;
		private static final int STR_LEN = 1024;
		
		public char msg[] = new char[STR_LEN];
		
		public MessagePackages(String m) {
			char[] str = m.toCharArray();
			if(str.length < STR_LEN) {
				for(int i = 0; i < str.length; i++)
					msg[i] = str[i];
			}
		}
		
		private static int size = -1;
		public static int size() {
			// TODO Auto-generated method stub
			if(size == -1)
				size = MessagePackages.toBytes(new MessagePackages("")).length;
			return size;
		}
	}
	
	private final static int TAG = 1;
	
	public interface IMessageService {
		public void onReceiveMessage(Session client, String msg);
	}

	public static class MessageServiceServer extends Service {
		private IMessageService mIMessageService = null;
		
		@Override
		public void handlePackages(Session client, SkyPackages pkg) {
			// TODO Auto-generated method stub
			MessagePackages msg = (MessagePackages)MessagePackages.toPackages(pkg.data);
			mIMessageService.onReceiveMessage(client, new String(msg.msg));
		}
	
		@Override
		public int getTag() {
			// TODO Auto-generated method stub
			return TAG;
		}
		
		public void setMessageServiceListener(IMessageService l) {
			mIMessageService = l;
		}
	}

	public static class MessageServiceClient extends Service {
		private IMessageService mIMessageService = null;
		
		@Override
		public void handlePackages(Session client, SkyPackages pkg) {
			// TODO Auto-generated method stub
			MessagePackages msg = (MessagePackages)MessagePackages.toPackages(pkg.data);
			mIMessageService.onReceiveMessage(client, new String(msg.msg));
		}
	
		@Override
		public int getTag() {
			// TODO Auto-generated method stub
			return TAG;
		}
		
		public void setMessageServiceListener(IMessageService l) {
			mIMessageService = l;
		}
		
		public void sendMessage(Session s, String msg) {
			s.sendPackages(createPackages(msg));
		}
	}
}
