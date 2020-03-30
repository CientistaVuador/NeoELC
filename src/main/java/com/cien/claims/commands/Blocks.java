package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Blocks extends CienCommandBase {

	public Blocks() {
		super("blocks", "Mostra seus blocos de claim");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Blocos de Claim: "+CienClaims.CLAIMS.getBlocksOf(player.getCommandSenderName()))));
	}

}
