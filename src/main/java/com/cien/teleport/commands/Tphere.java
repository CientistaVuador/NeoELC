package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Tphere extends CienCommandBase {

	public Tphere() {
		super("tphere", "Teleporta um player até você");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.tphere")) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
			return;
		}
		if (args.length == 0) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /tphere <Player>"));
			return;
		}
		EntityPlayerMP p = Util.getOnlinePlayerInexact(args[0]);
		if (p == null) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Player Offline ou inválido."));
			return;
		}
		Util.teleportPlayer(p, player.worldObj, (float)player.posX, (float)player.posY, (float)player.posZ, player.rotationPitch, player.rotationYaw);
		player.addChatMessage(Util.fixColors(Util.getPrefix()+"Sucesso!"));
	}

}
