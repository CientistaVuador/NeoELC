package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class ClaimAtual extends CienCommandBase {

	public ClaimAtual() {
		super("claimatual", "Mostra o claim em que você está");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim f = CienClaims.CLAIMS.getClaimInside(player);
		if (f == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você não está sobre nenhum claim.")));
			return;
		}
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Claim de "+f.getOwner()+", "+f.getWidth()+"x"+f.getLenght())));
	}

}
