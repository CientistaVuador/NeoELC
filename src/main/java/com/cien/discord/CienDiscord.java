package com.cien.discord;

import com.cien.data.Properties;

public class CienDiscord {

	public static final CienDiscord DISCORD = new CienDiscord();
	
	private final Properties prop = Properties.getProperties("(Module)CienDiscord");
	
	private CienDiscord() {
		
	}
	
	public Properties getProp() {
		return prop;
	}

}
