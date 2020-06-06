package com.cien.invites.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.invites.CienInvites;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Invite extends CienCommandBase {

	public Invite() {
		super("invite", "Convida um amigo seu para o servidor!");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /invite <Nick>");
			Util.sendMessage(player, "§cAviso: O Nick deve ser EXATO com letras maiúsculas e minúsculas.");
			return;
		}
		String p = args[0];
		if (CienInvites.INVITES.alreadyJoinedServer(p)) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Esse player já entrou no servidor.");
			return;
		}
		if (CienInvites.INVITES.getInviteWith(p) != null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Esse player já foi convidado por outro player.");
			return;
		}
		CienInvites.INVITES.addInviteTo(player.getCommandSenderName(), p);
		Util.sendMessage(player, Util.getPrefix()+"Sucesso! Quando "+p+" entrar você receberá as recompensas!");
	}

}
