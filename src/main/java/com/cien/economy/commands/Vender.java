package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.economy.ChestShop;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Vender extends CienCommandBase {

	public Vender() {
		super("v", "Vende um item.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Properties prop = Properties.getProperties(player.getCommandSenderName());
		ChestShop shop = (ChestShop) prop.getMemory("SHOP_ACTION");
		if (shop == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Clique com o direito na loja.");
			return;
		}
		if (shop.isBuy()) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Essa loja não compra itens.");
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
			Util.sendMessage(player, Util.getPrefix()+"Venda cancelada.");
			prop.setMemory("SHOP_ACTION", null);
			return;
		}
		int transfered = 0;
		for (int i = 0; i < quantity; i++) {
			if (CienEconomy.ECONOMY.canRemovePlayerMoney(shop.getOwner(), shop.getPrice()) || shop.isUnlimited()) {
				if (shop.transferOneToChest(player)) {
					CienEconomy.ECONOMY.addPlayerMoney(player.getCommandSenderName(), shop.getPrice());
					if (!shop.isUnlimited()) {
						CienEconomy.ECONOMY.removePlayerMoney(shop.getOwner(), shop.getPrice());
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
				Util.sendMessage(owner, Util.getPrefix()+player.getCommandSenderName()+" Vendeu "+transfered+" de "+shop.getItem().getDisplayName()+" com um valor de C$ "+value.toFormattedString());
			}
			Util.sendMessage(player, Util.getPrefix()+""+transfered+" itens foram vendidos!");
			Util.sendMessage(player, Util.getPrefix()+"com um valor de C$ "+value.toFormattedString());
		}
	}

}
