package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class SetBlockedItem extends CienCommandBase {

	public SetBlockedItem() {
		super("setblockeditem", "Bloqueia um item de ser usado perto de um claim.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.setblockeditem")) {
			player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão.")));
		} else {
			ItemStack hand = player.getCurrentEquippedItem();
			if (hand == null) {
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Coloque um item na mão.")));
				return;
			}
			boolean blocked = false;
			if (!CienClaims.CLAIMS.hasBlockedItem(Util.getItemNameID(hand.getItem())+":"+hand.getItemDamage())) {
				blocked = true;
				CienClaims.CLAIMS.setBlockedItem(Util.getItemNameID(hand.getItem())+":"+hand.getItemDamage(), true);
			} else {
				CienClaims.CLAIMS.setBlockedItem(Util.getItemNameID(hand.getItem())+":"+hand.getItemDamage(), false);
			}
			if (blocked) {
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Item Bloqueado Com Sucesso!")));
			} else {
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Item Desbloqueado Com Sucesso!")));
			}
		}
	}

}
