package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Money extends CienCommandBase {

	public Money() {
		super("money", "Mostra seu dinheiro.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			player.addChatComponentMessage(Util.fixColors(Util.getPrefix()+"C$ "+CienEconomy.ECONOMY.getPlayerMoney(player.getCommandSenderName())));
		} else {
			String f = Util.getPlayerInexact(args[0]);
			if (f == null) {
				player.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"Player Inválido."));
			} else {
				LongDecimal money = CienEconomy.ECONOMY.getPlayerMoney(f);
				player.addChatComponentMessage(Util.fixColors(Util.getPrefix()+"Dinheiro de "+f+": C$ "+money.toString()));
			}
		}
	}

}
