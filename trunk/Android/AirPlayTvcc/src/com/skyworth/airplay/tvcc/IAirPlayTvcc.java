package com.skyworth.airplay.tvcc;

import com.skyworth.airplay.framework.AirPlay.SenderInfo;



public interface IAirPlayTvcc {
	
	public void searchGetRequest(SenderInfo sender);
	public void connectGetRequest(SenderInfo sender);
	public void disconnectGetRequest(SenderInfo sender);
	
	public void clientinfoGetInfo(SenderInfo sender, int c);
}
