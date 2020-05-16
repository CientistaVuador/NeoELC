package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.teleport.CienTeleport;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class DelHome extends CienCommandBase {

	public DelHome() {
		super("delhome", "Deleta uma home");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length < 1) {
			sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /delhome <Nome>"));
		} else {
			com.cien.teleport.Home h = CienTeleport.TELEPORT.getHome(args[0], player.getCommandSenderName());
			if (h == null) {
				sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Essa home não existe."));
			} else {
				CienTeleport.TELEPORT.removeHome(h);
				sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Removida com Sucesso!"));
			}
		}
	}

}
