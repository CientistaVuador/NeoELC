package com.cien.commands;

import com.cien.Util;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class Memory extends CommandBase {

	@Override
	public String getCommandName() {
		return "memory";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "Mostra a memória do servidor";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] p_71515_2_) {
		sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Memória do servidor:")));
		long max = Runtime.getRuntime().maxMemory()/1000000;
		long free = Runtime.getRuntime().freeMemory()/1000000;
		long used = max - free;
		sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6Máximo: "+max+" MB")));
		sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6Livre: "+free+" MB")));
		sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6Usado: "+used+" MB")));
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

}
