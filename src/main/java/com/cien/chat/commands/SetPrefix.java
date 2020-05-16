package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class SetPrefix extends CienCommandBase {

	public SetPrefix() {
		super("setprefix", "Altera o seu prefixo");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.setprefix")) {
			if (args.length == 0) {
				CienChat.CHAT.setPlayerPrefix(player.getCommandSenderName(), null);
			} else {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < args.length; i++) {
					builder.append(args[i]);
					if (i != (args.length - 1)) {
						builder.append(' ');
					}
				}
				String prefix = builder.toString();
				if (Util.getRealLenghtOfMessage(prefix) > 16) {
					player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"O máximo é 16 caracteres"));
					return;
				}
				CienChat.CHAT.setPlayerPrefix(player.getCommandSenderName(), prefix);
			}
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Prefixo alterado com Sucesso!"));
		} else {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
		}
	}

}
