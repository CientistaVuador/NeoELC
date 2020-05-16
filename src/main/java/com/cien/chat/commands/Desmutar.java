package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Desmutar extends CienCommandBase {

	public Desmutar() {
		super("desmutar", "Desmuta um player");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.desmutar")) {
			if (args.length < 1) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /desmutar <Player>"));
			} else {
				String pl = args[0];
				if (!Util.isOnline(pl)) {
					player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Player não online ou inválido."));
					return;
				}
				long left = CienChat.CHAT.getMutedTimeLeft(pl);
				if (left <= 0) {
					player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Este player não está mutado."));
					return;
				}
				CienChat.CHAT.setMutedTimeLeft(pl, 0);
				player.addChatMessage(Util.fixColors(Util.getPrefix()+"Sucesso!"));
			}
		} else {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
		}
	}

}
