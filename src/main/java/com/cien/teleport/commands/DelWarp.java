package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import com.cien.teleport.CienTeleport;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class DelWarp extends CienCommandBase {

	public DelWarp() {
		super("delwarp", "Deleta uma warp");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "admin.delwarp")) {
			sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
			return;
		}
		if (args.length < 1) {
			sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /delwarp <Nome>"));
		} else {
			String nome = args[0];
			com.cien.teleport.Warp w = CienTeleport.TELEPORT.getWarp(nome);
			if (w == null) {
				sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Essa warp não existe."));
			} else {
				CienTeleport.TELEPORT.removeWarp(w);
				sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Warp removida com Sucesso!"));
			}
		}
	}

}
