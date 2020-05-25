package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class Cloja extends CienCommandBase {

	public Cloja() {
		super("cloja", "Cria um baú de vendas ou compras.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "exclusive.cloja")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão");
			return;
		}
		Properties prop = Properties.getProperties(player.getCommandSenderName());
		if (prop.getMemory("SHOP_TO_BUILD") != null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Aguardando seleção do bloco...");
			return;
		}
		if (args.length < 3) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /cloja <Venda/Compre> <Preço> <ManterNBT(true/false)> [Ilimitado(true/false)]");
		} else {
			String mode = args[0].toLowerCase();
			boolean buy = false;
			switch (mode) {
			case "venda":
				buy = false;
				break;
			case "compre":
				buy = true;
				break;
			default:
				Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /cloja <Venda/Compre> <Preço> <ManterNBT(true/false)>");
				return;
			}
			boolean unlimited = false;
			if (args.length > 3) {
				unlimited = Boolean.parseBoolean(args[3]);
			}
			if (unlimited) {
				if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.cloja")) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Sem permissão para fazer lojas infinitas.");
					return;
				}
			}
			LongDecimal preco = LongDecimal.parse(args[1]);
			boolean nbt = Boolean.parseBoolean(args[2]);
			ItemStack hand = player.getCurrentEquippedItem();
			if (hand == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Coloque o item em sua mão.");
				return;
			}
			Object[] buildShop = {buy, preco, nbt, hand.copy(), unlimited};
			prop.setMemory("SHOP_TO_BUILD", buildShop);
			Util.sendMessage(player, Util.getPrefix()+"Clique no bloco em que você quer colocar a loja.");
			CienEconomy.ECONOMY.run(() -> {
				if (prop.getMemory("SHOP_TO_BUILD") == null) {
					return;
				}
				prop.setMemory("SHOP_TO_BUILD", null);
				EntityPlayerMP p = Util.getOnlinePlayer(prop.getName());
				if (p != null) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Construção de loja cancelada.");
				}
			}, 10*20);
		}
	}

}
