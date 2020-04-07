package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class BlockedItems extends CienCommandBase {

	public BlockedItems() {
		super("blockeditems", "Mostra os itens bloqueados perto de claims");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		String[] blocked = CienClaims.CLAIMS.blockedItems();
		if (blocked.length == 0) {
			player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Não há nenhum item bloqueado.")));
		} else {
			player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Itens Bloqueados: ")));
			for (String b:blocked) {
				String[] split = b.split(":");
				int id = Integer.parseInt(split[0]);
				int meta = Integer.parseInt(split[1]);
				ItemStack stack = new ItemStack(Item.getItemById(id), 1, meta);
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(" §6"+stack.getDisplayName())));
			}
		}
	}

}
