package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class VerFlags extends CienCommandBase {

	public VerFlags() {
		super("verflags", "Mostra as flags de um claim.");
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim c = CienClaims.CLAIMS.getClaimInside(player);
		if (c == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Fique sobre um claim.")));
			return;
		}
		if (!c.getOwner().equals(player.getCommandSenderName())) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você não é dono desse terreno.")));
			return;
		}
		String[] flags = c.getFlags();
		if (flags.length == 0) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Esse claim não possui flags.")));
		} else {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Flags de "+c.getId()+":")));
			for (String s:flags) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(" §6"+s)));
			}
		}
	}

}
