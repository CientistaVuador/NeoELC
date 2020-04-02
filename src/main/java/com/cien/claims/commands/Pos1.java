package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.data.Properties;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Pos1 extends CienCommandBase {

	public Pos1() {
		super("pos1", "Marca a primeira posição");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Primeira posição marcada")));
		Properties.getProperties(player.getCommandSenderName()).setMemory("pos1", new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ));
	}

}
