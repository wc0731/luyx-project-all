package com.skyworth.skyplay.framework.tcp.sendfile;

import java.io.IOException;

import com.skyworth.skyplay.framework.Connection;
import com.skyworth.skyplay.framework.SkyPackage;
import com.skyworth.skyplay.framework.TCPConnection;
import com.skyworth.skyplay.framework.TCPServer;
import com.skyworth.skyplay.framework.tcp.sendfile.SendFile.SendFilePackage;

public class SendFileServer extends TCPServer {
	public interface ISendFileServer {
		void onReceiveStart(Connection c, String name, long size);
		void onReceiving(Connection c, byte[] data, int progress);
		void onReceiveEnd(Connection c);
	}

	private ISendFileServer mISendFileServer = null;
	
	public SendFileServer(ISendFileServer ii) throws IOException {
		super(SendFile.SERVER_PORT);
		// TODO Auto-generated constructor stub
		mISendFileServer = ii;
	}
	
	private long size = 0;
	private long rcv = 0;

	@Override
	public void onReceivePackage(TCPConnection c, SkyPackage pkg) {
		// TODO Auto-generated method stub
		SendFilePackage sfPKG = SendFilePackage.toPackage(pkg.data);
		switch(sfPKG.cmd) {
			case START:
				mISendFileServer.onReceiveStart(c, sfPKG.name, sfPKG.size);
				size = sfPKG.size;
				rcv = 0;
				break;
			case SENDING:
				rcv += sfPKG.data.length;
				mISendFileServer.onReceiving(c, sfPKG.data, (int)((double)rcv*100.00/(double)size));
				break;
			case END:
				mISendFileServer.onReceiveEnd(c);
				break;
			default:
				break;
		}
	}
}
