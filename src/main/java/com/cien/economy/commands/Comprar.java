package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.economy.ChestShop;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Comprar extends CienCommandBase {

	public Comprar() {
		super("c", "Compra um item da loja.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Properties prop = Properties.getProperties(player.getCommandSenderName());
		ChestShop shop = (ChestShop) prop.getMemory("SHOP_ACTION");
		if (shop == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Clique com o direito na loja.");
			return;
		}
		if (!shop.isBuy()) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Essa loja não vende itens.");
			return;
		}
		if (!shop.isValid()) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Loja inválida.");
			return;
		}
		if (!shop.isWorldLoaded()) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Mundo descarregado.");
			return;
		}
		int quantity;
		if (args.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /c <Quantidade>");
			return;
		}
		try {
			quantity = Integer.parseInt(args[0]);
		} catch (Exception ex) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
			return;
		}
		if (quantity < 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"A Quantidade é negativa");
			return;
		}
		if (quantity == 0) {
			Util.sendMessage(player, Util.getPrefix()+"Compra cancelada.");
			prop.setMemory("SHOP_ACTION", null);
			return;
		}
		int transfered = 0;
		for (int i = 0; i < quantity; i++) {
			if (CienEconomy.ECONOMY.canRemovePlayerMoney(player.getCommandSenderName(), shop.getPrice())) {
				if (shop.transferOneToPlayer(player)) {
					CienEconomy.ECONOMY.removePlayerMoney(player.getCommandSenderName(), shop.getPrice());
					if (!shop.isUnlimited()) {
						CienEconomy.ECONOMY.addPlayerMoney(shop.getOwner(), shop.getPrice());
					}
					transfered++;
				}
			} else {
				break;
			}
		}
		prop.setMemory("SHOP_ACTION", null);
		if (transfered == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Espaço insuficiente, estoque insuficiente ou dinheiro insuficiente.");
		} else {
			LongDecimal value = shop.getPrice().multiplyBy(LongDecimal.valueOf(transfered));
			EntityPlayerMP owner = Util.getOnlinePlayer(shop.getOwner());
			if (owner != null) {
				Util.sendMessage(owner, Util.getPrefix()+player.getCommandSenderName()+" Comprou "+transfered+" de "+shop.getItem().getDisplayName()+" com um valor de C$ "+value.toFormattedString());
			}
			Util.sendMessage(player, Util.getPrefix()+""+transfered+" itens foram comprados!");
			Util.sendMessage(player, Util.getPrefix()+"com um valor de C$ "+value.toFormattedString());
		}
	}

}
