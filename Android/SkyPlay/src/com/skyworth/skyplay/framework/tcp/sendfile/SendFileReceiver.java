package com.skyworth.skyplay.framework.tcp.sendfile;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.skyworth.skyplay.framework.Session;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.TCPServer;
import com.skyworth.skyplay.framework.TCPSession;
import com.skyworth.skyplay.framework.Service.IServiceServer;
import com.skyworth.skyplay.framework.tcp.sendfile.SendFile.SendFilePackage;
import com.skyworth.skyplay.framework.tcp.sendfile.SendFile.Task;

public class SendFileReceiver extends TCPServer {

	public interface ISendFileServer {
		void onReceiveStart(Session c, Task task);
		void onReceiving(Session c, Task task, int len, byte[] data);
		void onReceiveEnd(Session c, Task task);
		void onReceiveStop(Session c, Task task);
	}

	private ISendFileServer mISendFileServer = null;
	private IServiceServer mIServiceServer = null;
	
	private ArrayList<Task> tasklist = new ArrayList<Task>();
	
	private String save_path = null;

	public SendFileReceiver(String path, ITCPServer is, ISendFileServer isfs, IServiceServer iss) throws IOException {
		super(SendFile.PORT, is);
		// TODO Auto-generated constructor stub
		mISendFileServer = isfs;
		mIServiceServer = iss;
		save_path = path;
	}
	
	private Task searchTask(long id) {
		for(int i = 0; i < tasklist.size(); i++) {
			if(tasklist.get(i).id == id)
				return tasklist.get(i);
		}
		return null;
	}

	@Override
	public void onReceivePackage(TCPSession c, SkyPackage pkg) {
		// TODO Auto-generated method stub
		try {
			Session session = mIServiceServer.onHandlePackageSession(pkg);
			if(session != null && session.equals(c.name, c.addr)) {
				SendFilePackage sfPKG = SendFilePackage.toPackage(pkg.data);
				Task task = searchTask(sfPKG.task.id);
				switch(sfPKG.cmd) {
					case START:
						File file = new File(save_path + "/" + sfPKG.task.name);
						if(file.exists())
							file.delete();
						sfPKG.task.fileOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(save_path + "/" + sfPKG.task.name)));
						tasklist.add(sfPKG.task);
						mISendFileServer.onReceiveStart(session, sfPKG.task);
						break;
					case SENDING:
						task.fileOut.write(sfPKG.data, 0, sfPKG.len);
						task.progress += sfPKG.data.length;
						mISendFileServer.onReceiving(session, task, sfPKG.len, sfPKG.data);
						break;
					case END:
						task.fileOut.close();
						task.name = save_path + "/" + task.name;
						mISendFileServer.onReceiveEnd(session, task);
						tasklist.remove(task);
						break;
					case STOP:
						task.fileOut.close();
						mISendFileServer.onReceiveStop(session, task);
						tasklist.remove(task);
						break;
					default:
						break;
				}
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
