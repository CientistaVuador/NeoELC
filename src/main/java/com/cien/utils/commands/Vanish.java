package com.cien.utils.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import com.cien.superchat.SuperChatClickProcessor;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Vanish extends CienCommandBase {

	public Vanish() {
		super("vanish", "Te torna invisivel.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "admin.vanish")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
			return;
		}
		boolean p = player.isInvisible();
		player.setInvisible(!p);
		if (!p) {
			SuperChatClickProcessor undo = SuperChatClickProcessor
					.createSingleUseProcessor("§a[Ficar Visível]", "§aClique para ficar visível.", (ICommandSender s) -> {
						EntityPlayerMP pl = (EntityPlayerMP) s;
						pl.setInvisible(false);
					});
			Util.sendMessage(player, Util.getPrefix()+"Você agora está invisível. "+undo.getSuperChatCommandText());
		} else {
			SuperChatClickProcessor undo = SuperChatClickProcessor
					.createSingleUseProcessor("§a[Ficar Invisível]", "§aClique para ficar invisível.", (ICommandSender s) -> {
						EntityPlayerMP pl = (EntityPlayerMP) s;
						pl.setInvisible(true);
					});
			Util.sendMessage(player, Util.getPrefix()+"Você agora está visível. "+undo.getSuperChatCommandText());
		}
	}

}
