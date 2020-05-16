package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import com.cien.teleport.CienTeleport;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class SetWarp extends CienCommandBase {

	public SetWarp() {
		super("setwarp", "Cria uma warp");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.setwarp")) {
			sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
			return;
		}
		if (args.length < 1) {
			sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /setwarp <Nome>"));
		} else {
			String nome = args[0];
			com.cien.teleport.Warp w = new com.cien.teleport.Warp(nome, player.worldObj.provider.getDimensionName(), (float)player.posX, (float)player.posY, (float)player.posZ, player.rotationPitch, player.rotationYaw);
			if (CienTeleport.TELEPORT.addWarp(w)) {
				sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Warp definida com Sucesso!"));
			} else {
				CienTeleport.TELEPORT.removeWarp(w);
				CienTeleport.TELEPORT.addWarp(w);
				sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Warp redefinida com Sucesso!"));
			}
		}
	}

}
