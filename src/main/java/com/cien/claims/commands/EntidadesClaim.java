package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;

public class EntidadesClaim extends CienCommandBase {

	public EntidadesClaim() {
		super("entidadesclaim", "Mostra as entidades dentro de um claim.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim c = CienClaims.CLAIMS.getClaimInside(player);
		if (c == null) {
			player.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"Fique dentro de um claim."));
			return;
		}
		Entity[] entidades = c.getEntities();
		if (entidades.length == 0) {
			player.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"Não há entidades nesse claim."));
		} else {
			player.addChatComponentMessage(Util.fixColors(Util.getPrefix()+"Entidades em "+c.getId()+":"));
			for (Entity e:entidades) {
				player.addChatComponentMessage(Util.fixColors(" §6"+EntityList.getEntityString(e)));
			}
		}
		
	}

}
