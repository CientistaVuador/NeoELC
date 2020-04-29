package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class TransferirClaim extends CienCommandBase {

	public TransferirClaim() {
		super("transferirclaim", "Transfere o seu claim para outro player.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim f = CienClaims.CLAIMS.getClaimInside(player);
		if (args.length != 1) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /transferirclaim <Player>")));
			return;
		}
		if (f == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você não está sobre nenhum claim.")));
			return;
		}
		if (!f.getOwner().equals(player.getCommandSenderName())) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"O Claim não é seu.")));
			return;
		}
		EntityPlayerMP transferir = Util.getOnlinePlayer(args[0]);
		if (transferir == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player Inválido ou Offline")));
			return;
		}
		f.setOwner(transferir.getCommandSenderName());
		f.removeAllFlagsWith(player.getCommandSenderName());
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Claim transferido.")));
	}

}
