package com.cien.vip;

import cpw.mods.fml.common.eventhandler.Event;

public class VipDeactivationEvent extends Event {
	private final String player;
	
	public VipDeactivationEvent(String player) {
		this.player = player;
	}
	
	public String getPlayer() {
		return player;
	}
}
