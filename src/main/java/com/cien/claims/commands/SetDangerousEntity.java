package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class SetDangerousEntity extends CienCommandBase {

	public SetDangerousEntity() {
		super("setdangerousentity", "Coloca uma entidade na lista de entidades perigosas.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.setdangerousentity")) {
			player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão.")));
		} else {
			if (args.length == 0) {
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /setdangerousentity <Entidade>")));
				return;
			}
			boolean blocked = false;
			if (!CienClaims.CLAIMS.hasDangerousEntity(args[0])) {
				blocked = true;
				CienClaims.CLAIMS.setDangerousEntity(args[0], true);
			} else {
				CienClaims.CLAIMS.setDangerousEntity(args[0], false);
			}
			if (blocked) {
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Entidade marcada como perigosa com Sucesso!")));
			} else {
				player.addChatComponentMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Entidade desmarcada como perigosa com Sucesso!")));
			}
		}
	}

}
