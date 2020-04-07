package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Trust extends CienCommandBase {

	public Trust() {
		super("trust", "Dá trust para alguém em seu terreno.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim f = CienClaims.CLAIMS.getClaimInside(player);
		if (f == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você não está sobre nenhum claim.")));
			return;
		}
		if (!f.getOwner().equals(player.getCommandSenderName())) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"O Claim não é seu.")));
			return;
		}
		if (args.length == 0) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /trust <Player>")));
			return;
		}
		f.setFlag("permitirEntrar#"+args[0], true);
		f.setFlag("permitirUsarBloco#"+args[0], true);
		f.setFlag("permitirUsarItem#"+args[0], true);
		f.setFlag("permitirColocar#"+args[0], true);
		f.setFlag("permitirQuebrar#"+args[0], true);
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
	}

}
