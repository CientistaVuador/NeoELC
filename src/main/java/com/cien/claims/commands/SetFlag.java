package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class SetFlag extends CienCommandBase {

	public SetFlag() {
		super("setflag", "Coloca uma flag no seu claim.");
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim c = CienClaims.CLAIMS.getClaimInside(player);
		if (args.length < 1) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /setflag <Flag>"));
			return;
		}
		if (c == null) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Fique sobre um claim."));
			return;
		}
		if (!c.getOwner().equals(player.getCommandSenderName())) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Você não é dono desse terreno."));
			return;
		}
		boolean removed = false;
		if (c.getFlag(args[0])) {
			removed = true;
			c.setFlag(args[0], false);
		} else {
			c.setFlag(args[0], true);
		}
		if (removed) {
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Flag Removida Com Sucesso!"));
		} else {
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Flag Adicionada Com Sucesso!"));
		}
	}

}
