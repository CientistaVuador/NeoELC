package com.cien.discord.discordcommands;

import com.cien.discord.CienDiscord;
import com.cien.discord.DiscordCommand;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Unregister extends DiscordCommand {

	public Unregister() {
		super("unregister", "Remove seu discord do servidor.");
	}

	@Override
	public void onCommand(User user, Message msg, String[] args, String playerName) {
		if (playerName == null) {
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Você não está registrado.");
		} else {
			CienDiscord.DISCORD.setDiscordID(playerName, 0);
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Sucesso!");
		}
	}

}
