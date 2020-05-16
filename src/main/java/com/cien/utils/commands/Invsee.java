package com.cien.utils.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Invsee extends CienCommandBase {

	public Invsee() {
		super("invsee", "Abre o inventário de um player.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "admin.invsee")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
			return;
		}
		if (args.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /invsee <Player>");
			return;
		}
		EntityPlayerMP target = Util.getOnlinePlayerInexact(args[0]);
		float accuracy = (args[0].length()*1f) / (target.getCommandSenderName().length()*1f);
		if (target == null || accuracy < 0.25) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Player inválido ou offline.");
			return;
		}
		Util.sendMessage(player, Util.getPrefix()+"Abrindo o inventário de "+target.getCommandSenderName());
	}

}
