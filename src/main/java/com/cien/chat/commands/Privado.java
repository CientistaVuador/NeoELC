package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.discord.CienDiscord;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Privado extends CienCommandBase {

	public Privado() {
		super("p", "Chat Privado");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "chat.privado")) {
			if (args.length < 2) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /p <Player> <Mensagem>"));
				return;
			}
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				if (i == 0) {
					continue;
				}
				builder.append(args[i]);
				if (i != (args.length - 1)) {
					builder.append(' ');
				}
			}
			String msg = builder.toString();
			EntityPlayerMP online = Util.getOnlinePlayerInexact(args[0]);
			if (CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "chat.colors")) {
				msg = msg.replace('&', '§');
			}
			if (online != null) {
				String messageReceiver = CienChat.CHAT.getPrivateChatMessageForReceiver(player.getCommandSenderName(), msg);
				String messageSender = CienChat.CHAT.getPrivateChatMessageForSender(online.getCommandSenderName(), msg);
				player.addChatMessage(Util.fixColors(messageSender));
				if (CienChat.CHAT.isIgnoringPlayer(online.getCommandSenderName(), player.getCommandSenderName())) {
					Util.sendMessage(online, Ignorar.IgnoredMessageSuperChatCommand.buildCommandForMessage(messageReceiver));
				} else {
					Util.sendMessage(online, messageReceiver);
				}
				for (EntityPlayerMP p:Util.getOnlinePlayers()) {
					if (p.getCommandSenderName().equals(sender.getCommandSenderName())) {
						continue;
					}
					if (p.getCommandSenderName().equals(online.getCommandSenderName())) {
						continue;
					}
					if (CienPermissions.PERMISSIONS.hasPermission(p.getCommandSenderName(), "chat.staff")) {
						p.addChatMessage(Util.fixColors("§7"+sender.getCommandSenderName()+" -> "+online.getCommandSenderName()+": "+msg));
					}
				}
				System.out.println("[PRIVADO] "+sender.getCommandSenderName()+" -> "+online.getCommandSenderName()+": "+builder.toString());
				CienChat.CHAT.setLastSenderFor(online.getCommandSenderName(), sender.getCommandSenderName());
				CienDiscord.DISCORD.sendStaffMessage("[PRIVADO] "+sender.getCommandSenderName()+" -> "+online.getCommandSenderName()+": "+Util.discordColorsToBlackAndWhite(msg));
			} else {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Player offline ou inválido."));
			}
		} else {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
		}
	}
}
