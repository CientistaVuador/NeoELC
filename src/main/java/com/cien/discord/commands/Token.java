package com.cien.discord.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.discord.CienDiscord;

import net.dv8tion.jda.api.entities.User;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Token extends CienCommandBase {

	public Token() {
		super("token", "Usa um token do discord.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /token <Token>");
		} else {
			String token = args[0];
			Long id = CienDiscord.DISCORD.consumeToken(token);
			if (id == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Token Inválido.");
			} else {
				long discordId = id;
				User s = CienDiscord.DISCORD.getUser(discordId);
				if (CienDiscord.DISCORD.hasDiscordID(player.getCommandSenderName()) && s != null) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Seu discord já está registrado, o seu nome no discord é "+s.getName());
				} else {
					CienDiscord.DISCORD.setDiscordID(player.getCommandSenderName(), discordId);
					Util.sendMessage(player, Util.getPrefix()+"Discord registrado com sucesso!");
				}
			}
		}
	}

}
