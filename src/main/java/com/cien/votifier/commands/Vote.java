package com.cien.votifier.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import com.cien.votifier.CienVotifier;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Vote extends CienCommandBase {

	public Vote() {
		super("vote", "Comando de vote");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			String link = CienVotifier.VOTIFIER.getLink();
			if (link == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Sem link de vote definido.");
			} else {
				Util.sendMessage(player, Util.getPrefix()+link);
			}
		} else {
			if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.vote")) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
				return;
			}
			CienVotifier.VOTIFIER.setLink(args[0]);
			Util.sendMessage(player, Util.getPrefix()+"Link alterado com Sucesso!");
		}
	}

}
