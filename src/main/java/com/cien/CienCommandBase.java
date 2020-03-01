package com.cien;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class CienCommandBase extends CommandBase {

	private final String name;
	private final String description;
	
	public CienCommandBase(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return description;
	}

	@Override
	public void processCommand(ICommandSender p, String[] args) {
		if (p instanceof EntityPlayerMP) {
			onCommand(p, (EntityPlayerMP)p, args);
		} else {
			onCommand(p, null, args);
		}
	}
	
	public abstract void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args);

}
