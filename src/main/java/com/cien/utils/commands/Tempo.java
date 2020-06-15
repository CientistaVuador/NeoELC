package com.cien.utils.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.utils.CienUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Tempo extends CienCommandBase {

	public Tempo() {
		super("tempo", "Mostra o tempo de jogo");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Util.sendMessage(player, Util.getPrefix()+"Informações de tempo:");
		
		long on = CienUtils.UTILS.getOnlineTimeOf(player.getCommandSenderName());
		
		long secs = (on/1000);
		long minutes = (on/1000)/60;
		long hours = ((on/1000)/60)/60;
		
		long secOn = secs - (minutes*60);
		long minuteOn = minutes - (hours*60);
		long hoursOn = hours;
		
		if (hoursOn <= 0 && minuteOn <= 0) {
			Util.sendMessage(player, " §6Você tem "+secOn+" seg(s) de jogo.");
		} else if (hoursOn <= 0) {
			Util.sendMessage(player, " §6Você tem "+minuteOn+" minutos");
			Util.sendMessage(player, " §6e "+secOn+" seg(s) de jogo.");
		} else {
			Util.sendMessage(player, " §6Você tem "+hoursOn+" horas,");
			Util.sendMessage(player, " §6"+minuteOn+" minutos");
			Util.sendMessage(player, " §6e "+secOn+" seg(s) de jogo.");
		}
	}

}
