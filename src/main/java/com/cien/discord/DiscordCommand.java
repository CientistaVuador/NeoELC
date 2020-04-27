package com.cien.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public abstract class DiscordCommand {

	private final String name;
	private final String description;
	
	public DiscordCommand(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void onCommand(User user, Message msg, String[] args, String playerName);
}
