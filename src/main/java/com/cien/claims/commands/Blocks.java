package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Blocks extends CienCommandBase {

	public Blocks() {
		super("blocks", "Mostra seus blocos de claim");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Blocos de Claim: "+CienClaims.CLAIMS.getBlocksOf(player.getCommandSenderName()))));
		} else {
			if (CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.blocks")) {
				if (args.length < 3) {
					player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /blocks <al/ad/re> <Player> <Blocos>")));
				} else {
					String op = args[0].toLowerCase();
					int blocks;
					try {
						blocks = Integer.parseInt(args[2]);
					} catch (NumberFormatException ex) {
						player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Erro: "+ex.getMessage())));
						return;
					}
					if (!Properties.hasProperties(args[1])) {
						player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player Inválido.")));
						return;
					}
					switch (op) {
					case "al":
						CienClaims.CLAIMS.setBlocksOf(player.getCommandSenderName(), blocks);
						break;
					case "ad":
						CienClaims.CLAIMS.addBlocksTo(player.getCommandSenderName(), blocks);
						break;
					case "re":
						CienClaims.CLAIMS.removeBlocksOf(player.getCommandSenderName(), blocks);
						break;
					default:
						player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /blocks <al/ad/re> <Player> <Blocos>")));
						return;
					}
					player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
				}
			} else {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão")));
			}
		}
	}

}
