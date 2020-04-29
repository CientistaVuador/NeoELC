package com.cien.vip.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import com.cien.vip.CienVIP;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Vip extends CienCommandBase {

	public Vip() {
		super("vi", "Comando de controle vip.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.vi")) {
			if (CienVIP.VIP.isVip(player.getCommandSenderName())) {
				if (CienVIP.VIP.isVipInfinity(player.getCommandSenderName())) {
					Util.sendMessage(player, Util.getPrefix()+"O Seu vip é infinito e não possui tempo.");
				} else {
					Util.sendMessage(player, Util.getPrefix()+"Tempo Restante: "+CienVIP.VIP.getTimeLeft(player.getCommandSenderName())/1000+" Segs");
				}
			} else {
				Util.sendMessage(player, Util.getErrorPrefix()+"Você não é vip.");
			}
		} else {
			if (args.length != 1) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /vi <setinfinityrole/setrole/setgroup/vip>");
			} else {
				String arg = args[0].toLowerCase();
				if (arg.equals("setinfinityrole")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /vi setinfinityrole <ID>");
					} else {
						long id;
						try {
							id = Long.parseLong(args[1]);
						} catch (NumberFormatException ex) {
							Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
							return;
						}
						CienVIP.VIP.setVipInfinityRoleDiscordID(id);
						Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					}
					return;
				}
				if (arg.equals("setrole")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /vi setrole <ID>");
					} else {
						long id;
						try {
							id = Long.parseLong(args[1]);
						} catch (NumberFormatException ex) {
							Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
							return;
						}
						CienVIP.VIP.setVipInfinityRoleDiscordID(id);
						Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					}
					return;
				}
				if (arg.equals("setgroup")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /vi setgroup <Grupo>");
					} else {
						String group = args[1];
						CienVIP.VIP.setVipGroup(group);
						Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					}
					return;
				}
				if (arg.equals("vip")) {
					if (CienVIP.VIP.isVip(player.getCommandSenderName())) {
						if (CienVIP.VIP.isVipInfinity(player.getCommandSenderName())) {
							Util.sendMessage(player, Util.getPrefix()+"O Seu vip é infinito e não possui tempo.");
						} else {
							Util.sendMessage(player, Util.getPrefix()+"Tempo Restante: "+CienVIP.VIP.getTimeLeft(player.getCommandSenderName())/1000+" Segs");
						}
					} else {
						Util.sendMessage(player, Util.getErrorPrefix()+"Você não é vip.");
					}
					return;
				}
				Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /vi <setinfinityrole/setrole/setgroup/vip>");
			}
		}
	}

}
