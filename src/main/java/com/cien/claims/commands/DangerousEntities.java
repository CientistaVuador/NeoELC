package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class DangerousEntities extends CienCommandBase {

	public DangerousEntities() {
		super("dangerousentities", "Mostra as entidades consideradas perigosas pelo Sistema de claims.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		String[] danger = CienClaims.CLAIMS.getDangerousEntities();
		if (danger.length == 0) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Entidades Consideradas Como Perigosas."));
			return;
		}
		player.addChatMessage(Util.fixColors(Util.getPrefix()+"Entidades Consideradas Como Perigosas:"));
		for (String s:danger) {
			player.addChatMessage(Util.fixColors(" §6"+s));
		}
	}

}
