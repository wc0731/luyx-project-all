package SkyPlay2.Server;

import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;

import SkyPlay2.Session;
import SkyPlay2.Session.ISession;
import SkyPlay2.SkyPackages;

public class ClientManager {
	public interface IClientManager {
		public abstract void onClientConnect(Session client);
		public abstract void onClientDisconnect(Session client);
	}
 
	private HashMap<String, Session> mClientList = null;
	 
	private IClientManager mClientManagerListener = null;
	 
	private static ClientManager mClientManager = null;
	 
	public static ClientManager getInstance() {
		if(mClientManager == null)
			mClientManager = new ClientManager();
		return mClientManager;
	}
	
	ClientManager() {
		mClientList = new HashMap<String, Session>();
	}
	 
	public void setClientManagerListener(IClientManager l) {
		mClientManagerListener = l;
	}
	 
	public void broadcast(SkyPackages pkg) {
		for(int i = 0; i < mClientList.size(); i++) {
		}
	}
	
	public Session addClient(Socket socket) {
		String uid = getUID(socket);
		if(mClientList.get(uid) == null) {
			Session c = new Session(socket, uid);
			mClientList.put(uid, c);
			mClientManagerListener.onClientConnect(c);
			return c;
		}
		return null;
	}
	
	public void removeClient(Session session) {
		mClientList.remove(session.getUID());
		mClientManagerListener.onClientDisconnect(session);
	}
	
	public final static String getUID(Socket socket) {
        char hexDigits[] = { '0', '1', '2', '3', '4',
                             '5', '6', '7', '8', '9',
                             'A', 'B', 'C', 'D', 'E', 'F' };
        String s = socket.getInetAddress().getHostAddress() + String.valueOf(socket.getPort());
        try {
            byte[] btInput = s.getBytes();
     //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
     //使用指定的字节更新摘要
            mdInst.update(btInput);
     //获得密文
            byte[] md = mdInst.digest();
     //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
}