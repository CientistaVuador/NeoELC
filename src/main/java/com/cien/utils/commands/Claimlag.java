package com.cien.utils.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.claims.Claim;
import com.cien.utils.CienUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Claimlag extends CienCommandBase {

	public Claimlag() {
		super("claimlag", "Mostra o nível de lag de um claim.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Claim c = CienClaims.CLAIMS.getClaimInside(player);
		if (c == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Fique sobre um claim.");
			return;
		}
		long time = CienUtils.UTILS.getMediumTickTimeOf(c);
		Util.sendMessage(player, Util.getPrefix()+time+" nanos/tile");
	}

}
