package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Node;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Item extends CienCommandBase {

	public Item() {
		super("item", "debug");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (player.getCurrentEquippedItem() == null) {
			return;
		}
		player.addChatComponentMessage(new ChatComponentText(Node.toString(Util.getNodeFromItemStack("debug", player.getCurrentEquippedItem(), true))));
	}

}
