package com.cien.economy.commands;

import java.util.Arrays;
import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Top extends CienCommandBase {

	public Top() {
		super("top", "Mostra os 10 mais ricos onlines.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		EntityPlayerMP[] on = Util.getOnlinePlayers();
		Arrays.sort(on, (EntityPlayerMP o1, EntityPlayerMP o2) -> {
			LongDecimal a1 = CienEconomy.ECONOMY.getPlayerMoney(o1.getCommandSenderName());
			LongDecimal a2 = CienEconomy.ECONOMY.getPlayerMoney(o2.getCommandSenderName());
			if (a1.equals(a2)) {
				return 0;
			}
			if (a1.isSmallerThan(a2)) {
				return -1;
			}
			if (a1.isBiggerThan(a2)) {
				return 1;
			}
			return 0;
		});
		if (on.length <= 1) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Apenas você está online.");
		} else {
			Util.sendMessage(player, Util.getPrefix()+"Top 10 Players Mais Ricos Online:");
			for (EntityPlayerMP p:on) {
				Util.sendMessage(player, " §6"+p.getCommandSenderName()+" - C$ "+CienEconomy.ECONOMY.getPlayerMoney(p.getCommandSenderName()).toString());
			}
		}
	}

}
