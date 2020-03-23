package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Mutar extends CienCommandBase {

	public Mutar() {
		super("mutar", "Muta um jogador de usar o chat global");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.mutar")) {
			if (args.length < 2) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /mutar <Player> <Tempo (Segundos)>")));
			} else {
				String pl = args[0];
				long time;
				try {
					time = Long.parseLong(args[1]) * 1000;
					if (time <= 0) {
						throw new NumberFormatException("Número negativo ou igual a zero");
					}
				} catch (NumberFormatException ex) {
					player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Erro: "+ex.getMessage())));
					return;
				}
				if (!Util.isOnline(pl)) {
					player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player não online ou inválido.")));
					return;
				}
				CienChat.CHAT.setMutedTimeLeft(pl, time);
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
			}
		} else {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão.")));
		}
	}

}
