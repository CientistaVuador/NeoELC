package com.cien.utils.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Lanterna extends CienCommandBase {

	public Lanterna() {
		super("lanterna", "Lanterna");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!hasEffect(player)) {
			if (CienEconomy.ECONOMY.canRemovePlayerMoney(player.getCommandSenderName(), LongDecimal.valueOf(5))) {
				CienEconomy.ECONOMY.removePlayerMoney(player.getCommandSenderName(), LongDecimal.valueOf(5));
				addEffect(player);
				Util.sendMessage(player, Util.getPrefix()+"Feito!");
			} else {
				Util.sendMessage(player, Util.getPrefix()+"Dinheiro Insuficiente (C$5)");
			}
		} else {
			Util.sendMessage(player, Util.getErrorPrefix()+"Aguarde.");
		}
	}
	
	private void addEffect(EntityPlayerMP player) {
		player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 5*60*20));
	}
	
	private boolean hasEffect(EntityPlayerMP player) {
		return player.isPotionActive(Potion.nightVision.id);
	}

}
