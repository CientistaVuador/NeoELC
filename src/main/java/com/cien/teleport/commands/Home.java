package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.teleport.CienTeleport;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class Home extends CienCommandBase {

	public Home() {
		super("home", "Vai até uma home sua");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			com.cien.teleport.Home[] h = CienTeleport.TELEPORT.getHomes(sender.getCommandSenderName());
			if (h.length == 0) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bVocê não possui nenhuma home.")));
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Homes:")));
				for (com.cien.teleport.Home f:h) {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6"+f.getName())));
				}
			}
		} else {
			com.cien.teleport.Home h = CienTeleport.TELEPORT.getHome(args[0], player.getCommandSenderName());
			if (h == null) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bNome inválido.")));
			} else {
				WorldServer world = Util.getWorld(h.getWorld());
				if (world != null) {
					player.setWorld(world);
					player.setPositionAndRotation(h.getX(), h.getY(), h.getZ(), h.getYaw(), h.getPitch());
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Teleportado!")));
				} else {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bMundo não carregado ou inválido.")));
				}
			}
		}
	}

}
