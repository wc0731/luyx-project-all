package com.skyworth.airplay.framework;

import com.skyworth.airplay.framework.AirPlay.SenderInfo;

public interface IAirPlay {
	public void getUPPackage(SenderInfo sender);
	public void getSharePackage(SenderInfo sender);
	public void getDOWNPackage(SenderInfo sender);
	public void fileReceived(SenderInfo sender, String path, int index);
	public void heartbeat(SenderInfo sender);
}
