package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import com.cien.teleport.CienTeleport;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class SetHome extends CienCommandBase {

	public SetHome() {
		super("sethome", "Define uma home");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length < 1) {
			sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /sethome <Nome>"));
		} else {
			String nome = args[0];
			com.cien.teleport.Home w = new com.cien.teleport.Home(nome, player.getCommandSenderName(), player.worldObj.provider.getDimensionName(), (float)player.posX, (float)player.posY, (float)player.posZ, player.rotationPitch, player.rotationYaw);
			
			if (!CienTeleport.TELEPORT.containsHome(w.getName(), w.getOwner())) {
				if (CienTeleport.TELEPORT.getNumberOfHomes(player.getCommandSenderName()) < CienTeleport.TELEPORT.getMaxHomes(player.getCommandSenderName()) || CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.infinityhomes")) {
					sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Home definida com Sucesso!"));
					CienTeleport.TELEPORT.addHome(w);
				} else {
					sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Número máximo de homes atingido. ("+CienTeleport.TELEPORT.getMaxHomes(player.getCommandSenderName())+")"));
				}
			} else {
				CienTeleport.TELEPORT.removeHome(w);
				CienTeleport.TELEPORT.addHome(w);
				sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Home redefinida com Sucesso!"));
			}
		}
	}

}
