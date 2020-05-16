package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Enviar extends CienCommandBase {

	public Enviar() {
		super("enviar", "Envia dinheiro para alguém.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length != 2) {
			player.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /enviar <Player> <Quantidade>"));
		} else {
			if (Util.isOnline(args[0])) {
				EntityPlayerMP receptor = Util.getOnlinePlayerInexact(args[0]);
				LongDecimal dinheiro = LongDecimal.parse(args[1]);
				if (dinheiro.floatValue() <= 0) {
					player.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"O Dinheiro não pode ser zero ou menor que zero."));
					return;
				}
				if (CienEconomy.ECONOMY.removePlayerMoney(player.getCommandSenderName(), dinheiro)) {
					CienEconomy.ECONOMY.addPlayerMoney(receptor.getCommandSenderName(), dinheiro);
					player.addChatComponentMessage(Util.fixColors(Util.getPrefix()+"Sucesso!"));
					receptor.addChatComponentMessage(Util.fixColors(Util.getPrefix()+"Você recebeu C$ "+dinheiro.toString()+" de "+player.getCommandSenderName()));
				} else {
					player.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"Dinheiro Insuficiente."));
				}
			} else {
				player.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"Player Offline ou Inválido."));
			}
		}
	}

}
