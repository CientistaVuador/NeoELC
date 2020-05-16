package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class MeusClaims extends CienCommandBase {

	public MeusClaims() {
		super("meusclaims", "Mostra os seus claims.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim[] claims = CienClaims.CLAIMS.getClaims(player.getCommandSenderName());
		if (claims.length == 0) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Você não tem claims."));
		} else {
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Seus Claims: "));
			for (com.cien.claims.Claim c:claims) {
				player.addChatMessage(Util.fixColors(" §6ID: "+c.getId()+", X: "+c.getBiggerX()+", Z: "+c.getBiggerZ()+", M: "+c.getWorld()+", "+c.getWidth()+"x"+c.getLenght()));
			}
		}
	}

}
