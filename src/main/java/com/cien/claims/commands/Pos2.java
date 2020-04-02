package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.data.Properties;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Pos2 extends CienCommandBase {

	public Pos2() {
		super("pos2", "Marca a segunda posição");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Segunda posição marcada")));
		Properties.getProperties(player.getCommandSenderName()).setMemory("pos2", new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ));
	}

}
