package com.cien.levels.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.levels.CienLevels;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Level extends CienCommandBase {

	public Level() {
		super("level", "Mostra seu nivel atual.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		int level = CienLevels.LEVELS.getLevelOf(player.getCommandSenderName());
		long xp = CienLevels.LEVELS.getXPOf(player.getCommandSenderName());
		long next = CienLevels.LEVELS.getNextLevelRequiredXPFor(player.getCommandSenderName());
		
		Util.sendMessage(player, Util.getPrefix()+"Informações:");
		Util.sendMessage(player, "§6Nível Atual: "+level);
		Util.sendMessage(player, "§6XP Atual: "+xp);
		Util.sendMessage(player, "§6Próximo Nível: "+next);
	}

}
