package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class ETrust extends CienCommandBase {

	public ETrust() {
		super("etrust", "Dá trust de entrada para alguém em seu terreno.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim f = CienClaims.CLAIMS.getClaimInside(player);
		if (f == null) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Você não está sobre nenhum claim."));
			return;
		}
		if (!f.getOwner().equals(player.getCommandSenderName())) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"O Claim não é seu."));
			return;
		}
		if (args.length == 0) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /etrust <Player>"));
			return;
		}
		f.setFlag("permitirEntrar#"+args[0], true);
		player.addChatMessage(Util.fixColors(Util.getPrefix()+"Sucesso!"));
	}

}
