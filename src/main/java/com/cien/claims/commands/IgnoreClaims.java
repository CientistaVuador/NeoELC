package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class IgnoreClaims extends CienCommandBase {

	public IgnoreClaims() {
		super("ignoreclaims", "Ignora claims.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.ignoreclaims")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão");
			return;
		}
		boolean b = CienClaims.CLAIMS.isIgnoringClaims(player.getCommandSenderName());
		if (b) {
			CienClaims.CLAIMS.setIgnoringClaims(player.getCommandSenderName(), false);
			Util.sendMessage(player, Util.getPrefix()+"Você não está mais ignorando claims.");
		} else {
			CienClaims.CLAIMS.setIgnoringClaims(player.getCommandSenderName(), true);
			Util.sendMessage(player, Util.getPrefix()+"Você agora está ignorando claims.");
		}
	}

}
