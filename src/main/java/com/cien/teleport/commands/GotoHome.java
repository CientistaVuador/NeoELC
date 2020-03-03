package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;
import com.cien.teleport.CienTeleport;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class GotoHome extends CienCommandBase {

	public GotoHome() {
		super("gotohome", "Vai até uma home de um player");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "admin.gotohome")) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bSem Permissão.")));
			return;
		}
		if (args.length < 1) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bUso: /gotohome <Player> <Home>")));
		} else {
			String playr = args[0];
			String home = null;
			if (args.length >= 2) {
				home = args[1];
			}
			if (!Properties.hasProperties(playr)) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bEsse player não existe.")));
			} else {
				com.cien.teleport.Home[] homes = CienTeleport.TELEPORT.getHomes(playr);
				if (homes.length == 0) {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bEsse player não possui homes.")));
				} else {
					if (home == null) {
						sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Homes de "+playr+":")));
						for (com.cien.teleport.Home h:homes) {
							sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6"+h.getName())));
						}
					} else {
						com.cien.teleport.Home f = CienTeleport.TELEPORT.getHome(home, playr);
						if (f == null) {
							sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bEssa home não existe.")));
						} else {
							WorldServer world = Util.getWorld(f.getWorld());
							if (world != null) {
								Util.teleportPlayer(player, world, f.getX(), f.getY(), f.getZ(), f.getPitch(), f.getYaw());
								sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Teleportado!")));
							} else {
								sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§bMundo não carregado ou inválido.")));
							}
						}
					}
				}
			}
		}
	}

}
