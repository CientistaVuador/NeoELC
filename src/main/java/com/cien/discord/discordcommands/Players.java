package com.cien.discord.discordcommands;

import com.cien.Util;
import com.cien.discord.CienDiscord;
import com.cien.discord.DiscordCommand;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.entity.player.EntityPlayerMP;

public class Players extends DiscordCommand {

	public Players() {
		super("players", "Mostra os players onlines");
	}

	@Override
	public void onCommand(User user, Message msg, String[] args, String playerName) {
		EntityPlayerMP[] players = Util.getOnlinePlayers();
		StringBuilder builder = new StringBuilder(players.length*10);
		CienDiscord.DISCORD.sendCommandMessage("**"+players.length+" Player(s)**");
		for (int i = 0; i < players.length; i++) {
			builder.append(players[i].getCommandSenderName());
			if (i != (players.length-1)) {
				builder.append(", ");
			}
		}
		CienDiscord.DISCORD.sendCommandMessage(builder.toString());
	}

}
