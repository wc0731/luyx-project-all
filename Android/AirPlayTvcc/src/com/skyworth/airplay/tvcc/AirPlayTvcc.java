package com.skyworth.airplay.tvcc;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import com.skyworth.airplay.framework.AirPackage;
import com.skyworth.airplay.framework.AirPlay;
import com.skyworth.airplay.framework.IAirPlay;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class AirPlayTvcc extends AirPlay implements IAirPlayTvcc, IAirPlay {
	public class ClientInfo extends SenderInfo {
		private Timer heartTimer = new Timer();
		private int heartcount = 10;
		
		public ImageShare mImageShare = null;

		public ClientInfo(String a, int p) {
			super(a, p);
			// TODO Auto-generated constructor stub
		}
		
		public ClientInfo(SenderInfo s) {
			super(s.addr, s.port);
		}
		
		public void connect(Context c, int lr) {
			mImageShare = new ImageShare(c, lr);
			mMainLayerScene.addChild(mImageShare);
			
			heartTimer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	            	if(heartcount == 0) {
	            		heartbeatTimeout(ClientInfo.this);
	            		heartTimer.cancel();
	            	}
	            	else
	            		heartcount--;
	            }}, 0, 3000);
		}
		
		public void heartbeat() {
			heartcount = 10;
			sendHeart(addr, port);
		}
		
		public void disconnect() {
			heartTimer.cancel();
		}
	}
	
	private ClientInfo[] clientlist = {null, null};
	
	
	public static AssetManager assetManager = null;
	private CCGLSurfaceView glsurfaceView=null;
	private FrameLayout CocosLayer = null;
	private FrameLayout AndroidLayer = null;
	
	private MainLayerScene mMainLayerScene = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	
    	assetManager = getResources().getAssets();
    	
        CocosLayer = (FrameLayout)findViewById(R.id.CocosLayer);
        AndroidLayer = (FrameLayout)findViewById(R.id.AndroidLayer);
        
        glsurfaceView = new CCGLSurfaceView(this);
        CocosLayer.addView(glsurfaceView);

    	mMainLayerScene = new MainLayerScene();
        CCDirector.sharedDirector().attachInView(glsurfaceView);		
        CCDirector.sharedDirector().setDisplayFPS(false);
        CCDirector.sharedDirector().setAnimationInterval(1.0f/60);
        CCDirector.sharedDirector().runWithScene(mMainLayerScene.getScene());
        
        this.setIAirPlay(this);
        setSharePath("/skydir/airplay/");
        
        try {
			initAirPlay(SERVER_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CCDirector.sharedDirector().pause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CCDirector.sharedDirector().resume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		CCDirector.sharedDirector().end();
	}

	@Override
	protected void handle(DatagramPacket pkg) {
		// TODO Auto-generated method stub
		AirPackage pp = AirPackage.toPackage(pkg.getData());
		if(0 == pp.service) {
			SenderInfo sender = new SenderInfo(pkg.getAddress().getHostAddress(), pkg.getPort());
			Command cmd = Command.values()[pp.cmd];
			switch(cmd) {
				case CLIENTINFO:
					clientinfoGetInfo(sender, pp.len);
					break;
				case CLIENT_BROADCAST_SEARCH:
					searchGetRequest(sender);
					break;
				case CLIENT_REQUEST_CONNECT:
					connectGetRequest(sender);
					break;
				case CLIENT_REQUEST_DISCONNECT:
					disconnectGetRequest(sender);
					break;
				default:
					super.handle(pkg);
					break;
			}
		}
	}

	@Override
	public void getUPPackage(SenderInfo sender) {
		// TODO Auto-generated method stub
		ClientInfo ci = getClientInfo(sender);
		if(ci != null)
			ci.mImageShare.pressUp();
	}

	@Override
	public void getDOWNPackage(SenderInfo sender) {
		// TODO Auto-generated method stub
		ClientInfo ci = getClientInfo(sender);
		if(ci != null)
			ci.mImageShare.pressDown();
	}

	@Override
	public void getSharePackage(SenderInfo sender) {
		// TODO Auto-generated method stub
		ClientInfo ci = getClientInfo(sender);
		if(ci != null)
			ci.mImageShare.shareImage();
	}

	@Override
	public void fileReceived(SenderInfo sender, String path, int index) {
		// TODO Auto-generated method stub
		ClientInfo ci = getClientInfo(sender);
		if(ci != null)
			ci.mImageShare.newImage(path, index);
	}

	@Override
	public String getTargetAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTargetPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void heartbeat(SenderInfo sender) {
		// TODO Auto-generated method stub
		System.out.println("HEARTBEAT!!");
		ClientInfo ci = getClientInfo(sender);
		if(ci != null) 
			ci.heartbeat();
	}

	@Override
	public void searchGetRequest(SenderInfo sender) {
		// TODO Auto-generated method stub
		System.out.println("CLIENT_BROADCAST_SEARCH!!");
		AirPackage ap = new AirPackage();
		ap.addr = sender.addr;
		ap.port = sender.port;
		ap.service = 0;
		ap.cmd = Command.SERVER_SEARCH_RESPONSE.ordinal();
		sendPackage(ap);
	}

	@Override
	public void connectGetRequest(SenderInfo sender) {
		// TODO Auto-generated method stub
		System.out.println("CLIENT_REQUEST_CONNECT!!");

		ClientInfo ci = getClientInfo(sender);
		if(ci != null) 
			return;
		
		AirPackage ap = new AirPackage();
		ap.addr = sender.addr;
		ap.port = sender.port;
		ap.service = 0;
		ap.cmd = Command.SERVER_CONNECT_RESPONSE.ordinal();
		sendPackage(ap);
		
		for(int i = 0; i < clientlist.length; i++) {
			if(clientlist[i] == null) {
				clientlist[i] = new ClientInfo(sender);
				if(i == 0)
					clientlist[i].connect(this, ImageShare.ORIENTATIONLEFT);
				else
					clientlist[i].connect(this, ImageShare.ORIENTATIONRIGHT);
				break;
			}
		}
	}

	@Override
	public void disconnectGetRequest(SenderInfo sender) {
		// TODO Auto-generated method stub
		System.out.println("CLIENT_REQUEST_DISCONNECT!!");
		ClientInfo ci = getClientInfo(sender);
		if(ci != null) {
			ci.disconnect();
			ci = null;
			AirPackage ap = new AirPackage();
			ap.addr = sender.addr;
			ap.port = sender.port;
			ap.service = 0;
			ap.cmd = Command.SERVER_DISCONNECT_RESPONSE.ordinal();
			sendPackage(ap);
		}
	}
	
	public void heartbeatTimeout(ClientInfo c) {
		
	}

	@Override
	public void clientinfoGetInfo(SenderInfo sender, int c) {
		// TODO Auto-generated method stub
		System.out.println("CLIENTINFO!!");
		ClientInfo ci = getClientInfo(sender);
		if(ci != null)
			ci.mImageShare.setCount(c);
	}
	
	private ClientInfo getClientInfo(SenderInfo sender) {
		for(int i = 0; i < clientlist.length; i++) {
			if(clientlist[i] != null) {
				if(sender.getID().equals(clientlist[i].getID()))
					return clientlist[i];
			}
		}
		return null;
	}
}