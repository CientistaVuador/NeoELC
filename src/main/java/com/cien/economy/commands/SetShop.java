package com.cien.economy.commands;

import com.cien.CienCommandBase;
import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.claims.Claim;
import com.cien.economy.CienEconomy;
import com.cien.economy.Shop;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public class SetShop extends CienCommandBase {

	public SetShop() {
		super("setshop", "Cria uma loja e um teleporte.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "exclusive.setshop")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão");
			return;
		}
		if (args.length != 1) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /setshop <nome/remover>");
		} else {
			String arg = args[0];
			Shop shop = CienEconomy.ECONOMY.getShopOf(player.getCommandSenderName());
			if (arg.equals("remover")) {
				if (shop == null) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Você não tem loja definida.");
				} else {
					shop.disable();
					Util.sendMessage(player, Util.getPrefix()+"Loja removida.");
				}
			} else {
				float x = (float)player.posX;
				float y = (float)player.posY;
				float z = (float)player.posZ;
				float pitch = player.rotationPitch;
				float yaw = player.rotationYaw;
				String world = player.worldObj.provider.getDimensionName();
				String name = arg;
				Claim current = CienClaims.CLAIMS.getClaimInside(new PositiveLocation((int)x, (int)y, (int)z), (WorldServer)player.worldObj);
				if (current == null) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Fique sobre um claim seu.");
					return;
				}
				if (!current.getOwner().equals(player.getCommandSenderName())) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Esse claim não é seu.");
					return;
				}
				if (shop != null) {
					shop.enable(name, x, y, z, pitch, yaw, world);
				} else {
					Shop s = new Shop(name, player.getCommandSenderName(), x, y, z, world, pitch, yaw);
					CienEconomy.ECONOMY.addShop(s);
				}
				Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
			}
		}
	}

}
