package com.cien.utils.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Tiletick extends CienCommandBase {

	public Tiletick() {
		super("tiletick", "Força ticks em uma tile entity.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.tiletick")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
			return;
		}
		if (args.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /tiletick <Ticks>");
			return;
		}
		int ticks;
		try {
			ticks = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
			return;
		}
		Properties prop = Properties.getProperties(player.getCommandSenderName());
		prop.setMemory("TILETICK_", ticks);
		Util.sendMessage(player, Util.getPrefix()+"Clique com o direito em uma TileEntity para forçar ticks.");
	}

}
