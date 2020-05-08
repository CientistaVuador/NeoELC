package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Abandonclaim extends CienCommandBase {

	public Abandonclaim() {
		super("abandonclaim", "Abandona o claim atual.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim f = CienClaims.CLAIMS.getClaimInside(player);
		if (f == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você não está sobre nenhum claim.")));
			return;
		}
		if (!f.getOwner().equals(player.getCommandSenderName()) && !CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.abandonclaim")) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"O Claim não é seu.")));
			return;
		}
		CienClaims.CLAIMS.removeClaim(f);
		f.undoFences();
		f.getProperties().delete();
		CienClaims.CLAIMS.addBlocksTo(player.getCommandSenderName(), f.getSize());
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Claim removido e blocos retornados.")));
	}

}
