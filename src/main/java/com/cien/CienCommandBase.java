package com.cien;

import java.util.ArrayList;
import java.util.List;

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
	public int getRequiredPermissionLevel() {
		return 0;
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}
	
	@Override
	public List<?> addTabCompletionOptions(ICommandSender p_71516_1_, String[] arr) {
		List<String> str = new ArrayList<>();
		if (arr.length == 0) {
			return str;
		}
		for (int i = 0; i < (arr.length-1); i++) {
			str.add(arr[i]);
		}
		String toComplete = arr[arr.length-1];
		String player = Util.getOnlinePlayerInexact(toComplete).getCommandSenderName();
		if (player == null) {
			str.add(toComplete);
			return str;
		}
		str.add(player);
		return str;
	}
	
	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return Util.fixColorsOfMsg(Util.getPrefix()+"/"+name+" - "+description);
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
