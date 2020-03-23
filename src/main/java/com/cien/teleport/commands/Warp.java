package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.teleport.CienTeleport;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class Warp extends CienCommandBase {

	public Warp() {
		super("warp", "Teleporta até uma warp ou mostra a lista de warps");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length < 1) {
			com.cien.teleport.Warp[] warps = CienTeleport.TELEPORT.getWarps();
			if (warps.length == 0) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Não há nenhuma warp definida")));
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Warps:")));
				for (com.cien.teleport.Warp w:warps) {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(" §6"+w.getName())));
				}
			}
		} else {
			String name = args[0];
			if (CienTeleport.TELEPORT.containsWarp(name)) {
				com.cien.teleport.Warp w = CienTeleport.TELEPORT.getWarp(name);
				WorldServer world = Util.getWorld(w.getWorld());
				if (world != null) {
					Util.teleportPlayer(player, world, w.getX(), w.getY(), w.getZ(), w.getPitch(), w.getYaw());
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Teleportado!")));
				} else {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Mundo não carregado ou inválido.")));
				}
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Essa warp não existe.")));
			}
		}
	}

}
