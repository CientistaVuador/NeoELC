package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;
import com.cien.teleport.CienTeleport;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public class GotoHome extends CienCommandBase {

	public GotoHome() {
		super("gotohome", "Vai até uma home de um player");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), "admin.gotohome")) {
			sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Sem Permissão."));
			return;
		}
		if (args.length < 1) {
			sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /gotohome <Player> <Home>"));
		} else {
			String playr = args[0];
			String home = null;
			if (args.length >= 2) {
				home = args[1];
			}
			if (!Properties.hasProperties(playr)) {
				sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Esse player não existe."));
			} else {
				com.cien.teleport.Home[] homes = CienTeleport.TELEPORT.getHomes(playr);
				if (homes.length == 0) {
					sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Esse player não possui homes."));
				} else {
					if (home == null) {
						sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Homes de "+playr+":"));
						for (com.cien.teleport.Home h:homes) {
							sender.addChatMessage(Util.fixColors(" §6"+h.getName()));
						}
					} else {
						com.cien.teleport.Home f = CienTeleport.TELEPORT.getHome(home, playr);
						if (f == null) {
							sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Essa home não existe."));
						} else {
							WorldServer world = Util.getWorld(f.getWorld());
							if (world != null) {
								Util.teleportPlayer(player, world, f.getX(), f.getY(), f.getZ(), f.getPitch(), f.getYaw());
								sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Teleportado!"));
							} else {
								sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Mundo não carregado ou inválido."));
							}
						}
					}
				}
			}
		}
	}

}
