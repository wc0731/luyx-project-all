package com.skyworth.airplay.phone;

import java.util.ArrayList;

import com.skyworth.airplay.phone.AirPlayPhone.ServerInfo;

public interface IAirPlayPhone {
	public void searchGetResult(ArrayList<ServerInfo> l);
	public void connectGetResult();
	public void sendinfo();
	public void disconnectGetResult();
	
	public void heartbeatTimeout();
	
	public void cmd_doUP();
	public void cmd_share();
	public void cmd_doDOWN();
}
