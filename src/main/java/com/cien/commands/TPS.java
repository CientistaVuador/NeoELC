package com.cien.commands;

import com.cien.Util;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class TPS extends CommandBase {

	@Override
	public String getCommandName() {
		return "tps";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "Mostra o TPS do servidor";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] p_71515_2_) {
		sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"TPS: "+Util.getTPS())));
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}
}
