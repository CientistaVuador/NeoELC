package com.cien.discord.discordcommands;

import com.cien.discord.CienDiscord;
import com.cien.discord.DiscordCommand;
import com.cien.discord.DiscordCommandManager;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Ajuda extends DiscordCommand {

	public Ajuda() {
		super("ajuda", "Mostra os comandos.");
	}

	@Override
	public void onCommand(User user, Message msg, String[] args, String playerName) {
		DiscordCommand[] commands = DiscordCommandManager.getCommands();
		CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Comandos:");
		for (DiscordCommand cmd:commands) {
			CienDiscord.DISCORD.sendCommandMessage(": **elc->"+cmd.getName()+"** - "+cmd.getDescription());
		}
	}

}
