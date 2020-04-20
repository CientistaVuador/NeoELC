package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Money extends CienCommandBase {

	public Money() {
		super("money", "Mostra seu dinheiro.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"C$ "+CienEconomy.ECONOMY.getPlayerMoney(player.getCommandSenderName()))));
		} else {
			if (Properties.hasProperties(args[0])) {
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player Inválido.")));
			} else {
				LongDecimal money = CienEconomy.ECONOMY.getPlayerMoney(args[0]);
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Dinheiro de "+args[0]+": C$ "+money.toString())));
			}
		}
	}

}
