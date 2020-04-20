package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Eco extends CienCommandBase {

	public Eco() {
		super("eco", "Comando de Admin da Economia.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.eco")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
			return;
		}
		if (args.length != 3) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /eco <set/add/rem> <Player> <Dinheiro>");
		} else {
			String op = args[0];
			LongDecimal money = LongDecimal.parse(args[2]);
			if (Properties.hasProperties(args[1])) {
				switch (op) {
				case "set":
					onSet(player, args[1], money);
					return;
				case "add":
					onAdd(player, args[1], money);
					return;
				case "rem":
					onRem(player, args[1], money);
					return;
				}
				Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /eco <set/add/rem> <Player> <Dinheiro>");
			} else {
				Util.sendMessage(player, Util.getErrorPrefix()+"Player Inválido.");
			}
		}
	}
	
	private void onSet(EntityPlayerMP player, String receiver, LongDecimal money) {
		CienEconomy.ECONOMY.setPlayerMoney(receiver, money);
		Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
	}
	
	private void onAdd(EntityPlayerMP player, String receiver, LongDecimal money) {
		CienEconomy.ECONOMY.addPlayerMoney(receiver, money);
		Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
	}

	private void onRem(EntityPlayerMP player, String receiver, LongDecimal money) {
		CienEconomy.ECONOMY.setPlayerMoney(receiver, CienEconomy.ECONOMY.getPlayerMoney(receiver).minus(money));
		Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
	}

}
