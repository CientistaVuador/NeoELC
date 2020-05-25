package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.claims.CienClaims;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public class ClaimAtual extends CienCommandBase {

	public ClaimAtual() {
		super("claimatual", "Mostra o claim em que você está");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim f = CienClaims.CLAIMS.getClaimInside(player);
		boolean close = false;
		if (f == null) {
			close = true;
			f = CienClaims.CLAIMS.getClaimInsideShield(new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ), (WorldServer)player.worldObj);
			if (f == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Não há nenhum claim próximo.");
				return;
			}
		}
		if (!close) {
			Util.sendMessage(player, Util.getPrefix()+"("+f.getId()+") Claim de "+f.getOwner()+", "+f.getWidth()+"x"+f.getLenght());
		} else {
			Util.sendMessage(player, Util.getPrefix()+"(Próximo de você) ("+f.getId()+") Claim de "+f.getOwner()+", "+f.getWidth()+"x"+f.getLenght());
		}
	}

}
