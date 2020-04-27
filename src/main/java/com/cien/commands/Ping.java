package com.cien.commands;

import com.cien.CienCommandBase;
import com.cien.Util;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Ping extends CienCommandBase {

	public Ping() {
		super("ping", "Ping!");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Util.sendMessage(player, Util.getPrefix()+"Pong! ("+player.ping+"ms)");
	}

}
