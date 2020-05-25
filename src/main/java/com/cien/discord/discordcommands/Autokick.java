package com.cien.discord.discordcommands;

import com.cien.Util;
import com.cien.discord.CienDiscord;
import com.cien.discord.DiscordCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.entity.player.EntityPlayerMP;

public class Autokick extends DiscordCommand {

	public Autokick() {
		super("autokick", "Kika você do servidor.");
	}

	@Override
	public void onCommand(User user, Message msg, String[] args, String playerName) {
		if (playerName == null) {
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Você não está registrado.");
		} else {
			EntityPlayerMP on = Util.getOnlinePlayer(playerName);
			if (on == null) {
				CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Você não está online.");
			} else {
				on.playerNetServerHandler.kickPlayerFromServer("Autokikado pelo Discord.");
				CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Sucesso!");
			}
		}
	}

}
