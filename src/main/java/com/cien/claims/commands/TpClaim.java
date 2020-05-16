package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public class TpClaim extends CienCommandBase {

	public TpClaim() {
		super("tpclaim", "Teleporta até um claim.");
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /tpclaim <ID>"));
		} else {
			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Erro: "+ex.getMessage()));
				return;
			}
			com.cien.claims.Claim c = CienClaims.CLAIMS.getClaim(id);
			if (c == null) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"ID Inválido."));
				return;
			}
			if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.tpclaim")) {
				if (!c.getOwner().equals(player.getCommandSenderName())) {
					player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Esse claim não é seu."));
				}
			}
			int x = c.getBiggerX();
			int z = c.getBiggerZ();
			WorldServer world = Util.getWorld(c.getWorld());
			if (world == null) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Mundo inválido ou não carregado."));
				return;
			}
			int y = world.getHeightValue(x, z);
			Util.teleportPlayer(player, world, x, y, z, player.rotationPitch, player.rotationYaw);
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Teleportado Com Sucesso!"));
		}
	}

}
