package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Tpp extends CienCommandBase {

	public Tpp() {
		super("tpp", "Teleporta até um player");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.tpp")) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão.")));
			return;
		}
		if (args.length == 0) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /tpp <Player>")));
			return;
		}
		EntityPlayerMP p = Util.getOnlinePlayerInexact(args[0]);
		if (p == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player Offline ou inválido.")));
			return;
		}
		Util.teleportPlayer(player, p.worldObj, (float)p.posX, (float)p.posY, (float)p.posZ, p.rotationPitch, p.rotationYaw);
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
	}

}
