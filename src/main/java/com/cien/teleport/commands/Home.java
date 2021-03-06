package com.cien.teleport.commands;
import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.teleport.CienTeleport;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
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
				sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Você não possui nenhuma home."));
			} else {
				sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Homes:"));
				for (com.cien.teleport.Home f:h) {
					sender.addChatMessage(Util.fixColors(" §6"+f.getName()));
				}
			}
		} else {
			com.cien.teleport.Home h = CienTeleport.TELEPORT.getHome(args[0], player.getCommandSenderName());
			if (h == null) {
				sender.addChatMessage(Util.fixColors(Util.getPrefix()+"§bNome inválido."));
			} else {
				WorldServer world = Util.getWorld(h.getWorld());
				if (world != null) {
					Util.teleportPlayer(player, world, h.getX(), h.getY(), h.getZ(), h.getPitch(), h.getYaw());
					sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Teleportado!"));
				} else {
					sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Mundo não carregado ou inválido."));
				}
			}
		}
	}

}
