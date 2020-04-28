package com.cien.banitem.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.banitem.CienBanItem;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class BanItem extends CienCommandBase {

	public BanItem() {
		super("banitem", "Comando de banir itens.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.banitem")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
			return;
		}
		if (args.length != 2) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /banitem <banBlock/banItem> <meta(true/false)>");
		} else {
			String op = args[0].toLowerCase();
			boolean meta = Boolean.parseBoolean(args[1]);
			ItemStack hand = player.getCurrentEquippedItem();
			if (hand == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Coloque o item em sua mão.");
				return;
			}
			String name = Util.getItemNameID(hand.getItem());
			int itemMeta = hand.getItemDamage();
			
			String nameWithMeta = name+":"+itemMeta;
			String nameNoMeta = name+":*";
			
			String choose = nameWithMeta;
			if (!meta) {
				choose = nameNoMeta;
			}
			
			if (op.equals("banblock")) {
				if (CienBanItem.BANITEM.getBlockInteractionBanFor(choose)) {
					CienBanItem.BANITEM.setBlockInteractionBanFor(choose, false);
					Util.sendMessage(player, Util.getPrefix()+"Bloco desbanido.");
				} else {
					CienBanItem.BANITEM.setBlockInteractionBanFor(choose, true);
					Util.sendMessage(player, Util.getPrefix()+"Bloco banido.");
				}
				return;
			}
			if (op.equals("banitem")) {
				if (CienBanItem.BANITEM.getItemInteractionBanFor(choose)) {
					CienBanItem.BANITEM.setItemInteractionBanFor(choose, false);
					Util.sendMessage(player, Util.getPrefix()+"Item desbanido.");
				} else {
					CienBanItem.BANITEM.setItemInteractionBanFor(choose, true);
					Util.sendMessage(player, Util.getPrefix()+"Item banido.");
				}
				return;
			}
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /banitem <banBlock/banItem> <meta(true/false)>");
		}
	}

}
