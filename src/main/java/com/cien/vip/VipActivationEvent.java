package com.cien.vip;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;

public class VipActivationEvent extends Event {

	private final EntityPlayerMP player;
	private final Key k;
	
	public VipActivationEvent(EntityPlayerMP player, Key k) {
		this.player = player;
		this.k = k;
	}
	
	public Key getKey() {
		return k;
	}
	
	public EntityPlayerMP getPlayer() {
		return player;
	}
	
}
