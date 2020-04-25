package com.cien;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.command.ICommandSender;

@Cancelable
public class CommandPreprocessEvent extends Event {

	private final ICommandSender sender;
	private final String command;
	
	public CommandPreprocessEvent(ICommandSender sender, String command) {
		this.sender = sender;
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
	
	public ICommandSender getSender() {
		return sender;
	}

}
