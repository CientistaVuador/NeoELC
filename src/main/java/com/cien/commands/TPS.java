package com.cien.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class TPS extends CienCommandBase {

	public TPS() {
		super("tps", "Mostra o TPS do servidor");
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"TPS: "+Util.getTPS())));
	}
}
