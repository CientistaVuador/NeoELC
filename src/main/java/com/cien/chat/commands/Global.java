package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.discord.CienDiscord;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Global extends CienCommandBase {

	public Global() {
		super("g", "Chat Global");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "chat.global")) {
			if (args.length <= 0) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /g <Mensagem>")));
				return;
			}
			long muteTime = CienChat.CHAT.getMutedTimeLeft(player.getCommandSenderName());
			if (muteTime > 0) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você está mutado por "+muteTime/1000+" segundos.")));
			} else {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < args.length; i++) {
					builder.append(args[i]);
					if (i != (args.length - 1)) {
						builder.append(' ');
					}
				}
				String msg = builder.toString();
				if (CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "chat.colors")) {
					msg = msg.replace('&', '§');
				}
				EntityPlayerMP[] online = Util.getOnlinePlayers();
				String message = Util.fixColors(CienChat.CHAT.buildGlobalChatMessageFor(player.getCommandSenderName(), msg));
				for (EntityPlayerMP p:online) {
					if (CienPermissions.PERMISSIONS.hasPermission(p.getCommandSenderName(), "chat.global")) {
						p.addChatMessage(new ChatComponentText(message));
					}
				}
				System.out.println("[GLOBAL] "+sender.getCommandSenderName()+": "+builder.toString());
				String prefix = CienPermissions.PERMISSIONS.getGroupPrefixOf(player.getCommandSenderName());
				if (prefix == null) {
					prefix = "";
				}
				prefix = prefix.replace('&', '§');
				CienDiscord.DISCORD.sendGlobalMessage(Util.discordColorsToBlackAndWhite(prefix)+" "+player.getCommandSenderName()+": "+Util.discordColorsToBlackAndWhite(msg));
			}
		} else {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão.")));
		}
	}

}
