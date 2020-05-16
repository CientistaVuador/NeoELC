package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class SetNick extends CienCommandBase {

	public SetNick() {
		super("setnick", "Altera seu nick");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.setnick")) {
			if (args.length == 0) {
				CienChat.CHAT.setPlayerNick(player.getCommandSenderName(), null);
			} else {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < args.length; i++) {
					builder.append(args[i]);
					if (i != (args.length - 1)) {
						builder.append(' ');
					}
				}
				String nick = builder.toString();
				if (Util.getRealLenghtOfMessage(nick) > 16) {
					player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"O máximo é 16 caracteres"));
					return;
				}
				CienChat.CHAT.setPlayerNick(player.getCommandSenderName(), nick);
			}
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Nick alterado com Sucesso!"));
		} else {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
		}
	}

}
