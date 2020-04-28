package com.cien.discord.discordcommands;

import com.cien.discord.CienDiscord;
import com.cien.discord.DiscordCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Register extends DiscordCommand {

	public Register() {
		super("register", "Registra o seu discord no servidor com um token.");
	}

	@Override
	public void onCommand(User user, Message msg, String[] args, String playerName) {
		if (playerName != null) {
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Você já foi registrado, o seu nick é "+playerName);
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Remova seu registro com elc->unregister");
			return;
		}
		if (CienDiscord.DISCORD.hasToken(user.getIdLong())) {
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Um token já foi gerado.");
			return;
		} else {
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Enviei no privado, use com /token");
		}
		CienDiscord.DISCORD.sendPrivateMessage(user, "Entre no servidor e use /token "+CienDiscord.DISCORD.generateToken(user.getIdLong()));
	}

}
