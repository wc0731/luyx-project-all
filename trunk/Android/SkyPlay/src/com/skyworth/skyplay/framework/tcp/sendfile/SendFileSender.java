package com.skyworth.skyplay.framework.tcp.sendfile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.TCPOutSession;
import com.skyworth.skyplay.framework.TCPSession;
import com.skyworth.skyplay.framework.TCPSession.ITCPSession;
import com.skyworth.skyplay.framework.tcp.sendfile.SendFile.SendFilePackage;
import com.skyworth.skyplay.framework.tcp.sendfile.SendFile.SendFilePackage.COMMAND;
import com.skyworth.skyplay.framework.tcp.sendfile.SendFile.Task;

public class SendFileSender implements ITCPSession {
	public interface ISendFileSender {
		void onSendStart();
		void onSending(int progress);
		void onSendEnd();
		void onSendStop();
	}
	
	private class SendThread extends Thread {
		public void run() {
            try {
				DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(mfile)));
				SendFilePackage pkg = new SendFilePackage(COMMAND.START, task, 0, null);
				byte[] data = SendFilePackage.toBytes(pkg);
				System.out.println("data.len:" + data.length);
				SkyPackage skypkg = new SkyPackage(address, SendFile.PORT, data);
				mTCPOutSession.send(skypkg);
				mISendFileSender.onSendStart();

		        while (!isPaused) {
			        byte[] buf = new byte[SendFilePackage.PACKAGE_SIZE];
		            int read = 0;
		            if (fis != null)
		                read = fis.read(buf);
		            if (read == -1) {
			            pkg = new SendFilePackage(COMMAND.END, task, 0, null);
			            skypkg = new SkyPackage(address, SendFile.PORT, SendFilePackage.toBytes(pkg));
			            mTCPOutSession.send(skypkg);
			            mISendFileSender.onSendEnd();
		                break;
		            }
		            pkg = new SendFilePackage(COMMAND.SENDING, task, read, buf);
		            skypkg = new SkyPackage(address, SendFile.PORT, SendFilePackage.toBytes(pkg));
		            mTCPOutSession.send(skypkg);
		            task.progress += read;
		            mISendFileSender.onSending((int)(task.progress*100/task.size));
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	} 
	
	private ISendFileSender mISendFileSender = null;
	private TCPOutSession mTCPOutSession = null;
	private SendThread sendThread = null;
	private File mfile= null;
	private Task task = null;
	
	private boolean isStop = false;
	private boolean isPaused = false;
	
	private String address = null;
	
	public SendFileSender(String addr, File file, ISendFileSender sfs) throws UnknownHostException, IOException {
		mTCPOutSession = new TCPOutSession(new Socket(addr, SendFile.PORT), this);
		mfile = file;
		mISendFileSender = sfs;
		address = addr;
		task = new Task();
		task.id = (long)(Math.random()*Math.pow(2, 63));
		task.name = mfile.getName();
		task.size = mfile.length();
		task.progress = 0;
	}
	
	public void start() {
		task.progress = 0;
		sendThread = new SendThread();
		sendThread.start();
	}
	
	public void pause() {
		isPaused = !isPaused;
	}
	
	public void stop() throws IOException {
		isStop = true;
		SendFilePackage pkg = new SendFilePackage(COMMAND.STOP, task, 0, null);
		mTCPOutSession.send((SkyPackage)SkyPackage.toPackage(SendFilePackage.toBytes(pkg)));
	}

	@Override
	public void onReceivePackage(TCPSession s, SkyPackage pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClosed(TCPSession s) {
		// TODO Auto-generated method stub
		
	}
}
