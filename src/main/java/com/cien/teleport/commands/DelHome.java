package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.teleport.CienTeleport;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class DelHome extends CienCommandBase {

	public DelHome() {
		super("delhome", "Deleta uma home");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length < 1) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bUso: /delhome <Nome>")));
		} else {
			com.cien.teleport.Home h = CienTeleport.TELEPORT.getHome(args[0], player.getCommandSenderName());
			if (h == null) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bEssa home não existe.")));
			} else {
				CienTeleport.TELEPORT.removeHome(h);
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Removida com Sucesso!")));
			}
		}
	}

}
