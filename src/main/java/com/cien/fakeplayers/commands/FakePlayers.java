package com.cien.fakeplayers.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.fakeplayers.CienFakePlayers;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class FakePlayers extends CienCommandBase {

	public FakePlayers() {
		super("fakeplayers", "Mostra os fakeplayers.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		String[] fake = CienFakePlayers.FAKEPLAYERS.getFakePlayers();
		if (fake.length == 0) {
			Util.sendMessage(sender, Util.getErrorPrefix()+"Nenhum Fake Player foi encontrado.");
		} else {
			Util.sendMessage(sender, Util.getPrefix()+"Fake Players Detectados:");
			for (String f:fake) {
				Util.sendMessage(sender, " §6"+f);
			}
		}
	}

}
