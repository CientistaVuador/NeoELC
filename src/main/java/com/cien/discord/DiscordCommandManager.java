package com.cien.discord;

import java.util.ArrayList;
import java.util.List;

import com.cien.discord.discordcommands.Ajuda;
import com.cien.discord.discordcommands.Register;
import com.cien.discord.discordcommands.Unregister;

public class DiscordCommandManager {
	
	private static final List<DiscordCommand> commands = new ArrayList<>();
	
	static {
		register(new Ajuda());
		register(new Register());
		register(new Unregister());
	}
	
	public static DiscordCommand[] getCommands() {
		return commands.toArray(new DiscordCommand[commands.size()]);
	}
	
	public static DiscordCommand getCommand(String name) {
		for (DiscordCommand c:getCommands()) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	public static void register(DiscordCommand cmd) {
		commands.add(cmd);
	}
	
	private DiscordCommandManager() {
		
	}
}
