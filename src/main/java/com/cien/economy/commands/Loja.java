package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.economy.CienEconomy;
import com.cien.economy.Shop;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Loja extends CienCommandBase {

	public Loja() {
		super("loja", "Vai até a loja de um player.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			Shop[] shops = CienEconomy.ECONOMY.getShops();
			if (shops.length == 0) {
				Util.sendMessage(player, "Não há lojas criadas por players.");
			} else {
				Util.sendMessage(player, Util.getPrefix()+"Lojas: ");
				for (Shop s:shops) {
					if (s.isEnabled()) {
						String name = s.getName().replace('&', '§');
						if (s.isValid()) {
							Util.sendMessage(player, " §6"+s.getID()+" - "+name);
						} else {
							Util.sendMessage(player, " §4(Inválida) - "+name);
						}
					}
				}
				Util.sendMessage(player, Util.getPrefix()+"Use /loja <ID> para se teleportar.");
			}
		} else {
			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (Exception ex) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
				return;
			}
			Shop s = CienEconomy.ECONOMY.getShopByID(id);
			if (s == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Essa loja não existe.");
			} else {
				if (!s.isEnabled()) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Essa loja está desativada.");
				} else {
					if (!s.isValidFor(player.getCommandSenderName())) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Essa loja não possui um claim sobre ela ou possui o mundo descarregado ou você não tem permissão para entrar nela.");
					} else {
						Util.teleportPlayer(player, Util.getWorld(s.getWorld()), s.getX(), s.getY(), s.getZ(), s.getPitch(), s.getYaw());
						Util.sendMessage(player, Util.getPrefix()+"Teleportado!");
					}
				}
			}
		}
	}

}
