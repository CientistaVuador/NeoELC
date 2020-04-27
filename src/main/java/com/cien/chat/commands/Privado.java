package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.discord.CienDiscord;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Privado extends CienCommandBase {

	public Privado() {
		super("p", "Chat Privado");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "chat.privado")) {
			if (args.length < 2) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /p <Player> <Mensagem>")));
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
			String receiver = args[0];
			if (CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "chat.colors")) {
				msg = msg.replace('&', '§');
			}
			if (Util.isOnline(receiver)) {
				String messageReceiver = Util.fixColors(CienChat.CHAT.getPrivateChatMessageForReceiver(player.getCommandSenderName(), msg));
				String messageSender = Util.fixColors(CienChat.CHAT.getPrivateChatMessageForSender(receiver, msg));
				player.addChatMessage(new ChatComponentText(messageSender));
				Util.getOnlinePlayer(receiver).addChatMessage(new ChatComponentText(messageReceiver));
				for (EntityPlayerMP p:Util.getOnlinePlayers()) {
					if (p.getCommandSenderName().equals(sender.getCommandSenderName())) {
						continue;
					}
					if (p.getCommandSenderName().equals(receiver)) {
						continue;
					}
					if (CienPermissions.PERMISSIONS.hasPermission(p.getCommandSenderName(), "chat.staff")) {
						p.addChatMessage(new ChatComponentText(Util.fixColors("§7"+sender.getCommandSenderName()+" -> "+receiver+": "+msg)));
					}
				}
				System.out.println("[PRIVADO] "+sender.getCommandSenderName()+" -> "+receiver+": "+builder.toString());
				CienChat.CHAT.setLastSenderFor(receiver, sender.getCommandSenderName());
				CienDiscord.DISCORD.sendStaffMessage("[PRIVADO] "+sender.getCommandSenderName()+" -> "+receiver+": "+Util.discordColorsToBlackAndWhite(msg));
			} else {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player offline ou inválido.")));
			}
		} else {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão.")));
		}
	}
}
