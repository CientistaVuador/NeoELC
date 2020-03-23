package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;
import com.cien.teleport.CienTeleport;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class SetMaxHomes extends CienCommandBase {

	public SetMaxHomes() {
		super("setmaxhomes", "Altera o número máximo de homes de um player");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "admin.setmaxhomes")) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão")));
			return;
		}
		if (args.length < 2) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /setmaxhomes <Player> <Max Homes>")));
		} else {
			String playr = args[0];
			int maxHomes;
			try {
				maxHomes = Integer.parseInt(args[1]);
				if (maxHomes < 0) {
					throw new NumberFormatException("Não é permitido números negativos.");
				}
			} catch (NumberFormatException ex) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Erro: "+ex.getMessage())));
				return;
			}
			if (!Properties.hasProperties(playr)) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player Inválido.")));
				return;
			}
			CienTeleport.TELEPORT.setMaxHomes(playr, maxHomes);
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
		}
	}

}
