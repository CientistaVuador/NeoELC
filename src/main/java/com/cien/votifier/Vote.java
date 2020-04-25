package com.cien.votifier;

public class Vote {

	private final String service;
	private final String nick;
	private final String ip;
	private final String time;
	
	public Vote(String service, String nick, String ip, String time) {
		this.service = service;
		this.nick = nick;
		this.ip = ip;
		this.time = time;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getIp() {
		return ip;
	}
	
	public String getService() {
		return service;
	}
	
	public String getTime() {
		return time;
	}

}
