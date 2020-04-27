package com.cien.discord.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.discord.CienDiscord;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Discord extends CienCommandBase {

	public Discord() {
		super("discord", "Controle do Discord.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.discord")) {
			String invite = CienDiscord.DISCORD.getDiscordInvite();
			if (invite == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Nenhum convite foi definido.");
			} else {
				Util.sendMessage(player, Util.getPrefix()+invite);
			}
		} else {
			if (args.length == 0) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /discord <restart/invite/setinvite/setglobal/setvip/setcommand/setstaff/settoken>");
			} else {
				String arg = args[0];
				if (arg.equalsIgnoreCase("restart")) {
					Util.sendMessage(player, Util.getPrefix()+"Reiniciando...");
					try {
						CienDiscord.DISCORD.shutdown();
						CienDiscord.DISCORD.start();
					} catch (Exception ex) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
						return;
					}
					Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					return;
				}
				if (arg.equalsIgnoreCase("invite")) {
					String invite = CienDiscord.DISCORD.getDiscordInvite();
					if (invite == null) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Nenhum convite foi definido.");
					} else {
						Util.sendMessage(player, Util.getPrefix()+invite);
					}
					return;
				}
				if (arg.equalsIgnoreCase("setinvite")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /discord setinvite <Link>");
					} else {
						CienDiscord.DISCORD.setDiscordInvite(args[1]);
						Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					}
					return;
				}
				if (arg.equalsIgnoreCase("setglobal")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /discord setglobal <ID>");
					} else {
						try {
							CienDiscord.DISCORD.setGlobalChatID(Long.parseLong(args[1]));
						} catch (NumberFormatException ex) {
							Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
							return;
						}
						Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					}
					return;
				}
				if (arg.equalsIgnoreCase("setvip")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /discord setvip <ID>");
					} else {
						try {
							CienDiscord.DISCORD.setVipChatID(Long.parseLong(args[1]));
						} catch (NumberFormatException ex) {
							Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
							return;
						}
						Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					}
					return;
				}
				if (arg.equalsIgnoreCase("setcommand")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /discord setcommand <ID>");
					} else {
						try {
							CienDiscord.DISCORD.setCommandChatID(Long.parseLong(args[1]));
						} catch (NumberFormatException ex) {
							Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
							return;
						}
						Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					}
					return;
				}
				if (arg.equalsIgnoreCase("setstaff")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /discord setstaff <ID>");
					} else {
						try {
							CienDiscord.DISCORD.setStaffChatID(Long.parseLong(args[1]));
						} catch (NumberFormatException ex) {
							Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
							return;
						}
						Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
					}
					return;
				}
				if (arg.equalsIgnoreCase("settoken")) {
					if (args.length != 2) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /discord settoken <Token>");
					} else {
						CienDiscord.DISCORD.setToken(args[1]);
						Util.sendMessage(player, Util.getPrefix()+"Sucesso, é recomendado reiniciar o bot.");
					}
					return;
				}
				Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /discord <restart/invite/setinvite/setglobal/setvip/setcommand/setstaff/settoken>");
			}
		}
	}

}
