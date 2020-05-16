package com.cien.kits.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.kits.CienKits;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class Kit extends CienCommandBase {

	public Kit() {
		super("kit", "Comando de kits");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			com.cien.kits.Kit[] kits = CienKits.KITS.getKits();
			if (kits.length == 0) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Não há kits criados."));
			} else {
				player.addChatMessage(Util.fixColors(Util.getPrefix()+"Kits:"));
				for (com.cien.kits.Kit k:kits) {
					if (!k.hasPermission(player.getCommandSenderName())) {
						player.addChatMessage(Util.fixColors(" §c"+k.getName()));
					} else {
						long timeLeft = k.getTimeLeftFor(player.getCommandSenderName())/1000;
						if (timeLeft > 0) {
							player.addChatMessage(Util.fixColors(" §e"+k.getName()+" - "+timeLeft+"s"));
						} else {
							player.addChatMessage(Util.fixColors(" §6"+k.getName()));
						}
					}
				}
			}
		} else {
			com.cien.kits.Kit kit = CienKits.KITS.getKit(args[0]);
			if (kit == null) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Esse kit não existe."));
				return;
			}
			if (!kit.hasPermission(player.getCommandSenderName())) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem permissão."));
				return;
			}
			long timeLeft = kit.getTimeLeftFor(player.getCommandSenderName())/1000;
			if (timeLeft > 0) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Aguarde "+timeLeft+" segundos."));
				return;
			}
			kit.resetTimeLeftFor(player.getCommandSenderName());
			ItemStack[] items = kit.getItems();
			int index = 0;
			boolean inventoryFull = false;
			for (ItemStack s:items) {
				int slot = player.inventory.getFirstEmptyStack();
				if (slot == -1) {
					inventoryFull = true;
					break;
				}
				player.inventory.addItemStackToInventory(s);
				index++;
			}
			if (inventoryFull) {
				for (int i = index; i < items.length; i++) {
					player.entityDropItem(items[i], 0.5f);
				}
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Inventário cheio, itens dropados no chão."));
			}
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Sucesso!"));
		}
	}

}
