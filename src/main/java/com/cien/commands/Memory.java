package com.cien.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Memory extends CienCommandBase {

	public Memory() {
		super("memory", "Mostra a memória do servidor");
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Memória do servidor:")));
		long max = Runtime.getRuntime().maxMemory()/1000000;
		long free = Runtime.getRuntime().freeMemory()/1000000;
		long used = max - free;
		sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6Máximo: "+max+" MB")));
		sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6Livre: "+free+" MB")));
		sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6Usado: "+used+" MB")));
	}

}
