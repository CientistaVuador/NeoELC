package com.cien.permissions.commands;

import com.cien.Util;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class Perms extends CommandBase {

	@Override
	public String getCommandName() {
		return "perms";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "Controla o sistema de permissões";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length<1) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms <set/get/copy/reload>")));
		} else {
			String arg = args[0].toLowerCase();
			if (arg.equals("set")) {
				set(sender, args);
			} else if (arg.equals("get")) {
				get(sender, args);
			} else if (arg.equals("copy")) {
				copy(sender, args);
			} else if (arg.equals("reload")) {
				reload(sender, args);
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms <set/get/copy/reload>")));
			}
		}
	}
	
	private void reload(ICommandSender sender, String[] args) {
		if (args.length<2) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms reload <grupo>")));
		} else {
			String group = args[1];
			if (CienPermissions.PERMISSIONS.hasGroup(group)) {
				CienPermissions.PERMISSIONS.reload(group);
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Grupo Recarregado!")));
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo Inexistente.")));
			}
		}
	}
	
	private void copy(ICommandSender sender, String[] args) {
		if (args.length < 3) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms copy <grupo a> <grupo b>")));
		} else {
			String a = args[1];
			String b = args[2];
			if (CienPermissions.PERMISSIONS.hasGroup(a)) {
				if (CienPermissions.PERMISSIONS.hasGroup(b)) {
					String[] perms = CienPermissions.PERMISSIONS.getGroupPermissions(a);
					for (String s:perms) {
						CienPermissions.PERMISSIONS.setGroupPermission(b, s, true);
					}
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Copiado com Sucesso!")));
				} else {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo "+b+" não existe.")));
				}
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo "+a+" não existe.")));
			}
		}
	}
	
	private void get(ICommandSender sender, String[] args) {
		if (args.length < 2) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms get <groups/group/player>")));
		} else {
			String arg = args[1].toLowerCase();
			if (arg.equals("groups")) {
				groups(sender, args);
			} else if (arg.equals("group")) {
				group(sender, args);
			} else if (arg.equals("player")) {
				player(sender, args);
			} else if (arg.equals("default")) {
				get_default(sender, args);
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms get <groups/group/player>")));
			}
		}
	}
	
	private void groups(ICommandSender sender, String[] args) {
		String[] groups = CienPermissions.PERMISSIONS.getPermissionsGroups();
		if (groups.length==0) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Não há grupos disponíveis.")));
		} else {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Grupos:")));
			for (String s:groups) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors("§6 "+s)));
			}
		}
	}
	
	private void group(ICommandSender sender, String[] args) {
		if (args.length < 3) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms get group <grupo>")));
		} else {
			String arg = args[2];
			if (CienPermissions.PERMISSIONS.hasGroup(arg)) {
				String[] perms = CienPermissions.PERMISSIONS.getGroupPermissions(arg);
				if (perms.length==0) {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Esse grupo não possui nenhuma permissão.")));
				} else {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Permissões:")));
					for (String s:perms) {
						sender.addChatMessage(new ChatComponentText(Util.fixColors("§6 "+s)));
					}
				}
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo Inexistente.")));
			}
		}
	}
	
	private void player(ICommandSender sender, String[] args) {
		if (args.length < 3) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms get player <player>")));
		} else {
			String arg = args[2];
			if (Properties.hasProperties(arg)) {
				String group = CienPermissions.PERMISSIONS.getGroup(arg);
				if (group == null) {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Esse player não possui grupo")));
				} else {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Grupo de "+arg+" -> "+group)));
				}
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player Inexistente.")));
			}
		}
	}
	
	private void set(ICommandSender sender, String[] args) {
		if (args.length < 2) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set <group/perm/player/default>")));
		} else {
			String arg = args[1].toLowerCase();
			if (arg.equals("group")) {
				set_group(sender, args);
			} else if (arg.equals("perm")) {
				set_perm(sender, args);
			} else if (arg.equals("player")) {
				set_player(sender, args);
			} else if (arg.equals("default")) {
				set_default(sender, args);
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set <group/perm/player>")));
			}
		}
	}
	
	private void set_default(ICommandSender sender, String[] args) {
		if (args.length < 3) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set default <grupo>")));
		} else {
			String grupo = args[2];
			if (CienPermissions.PERMISSIONS.hasGroup(grupo)) {
				CienPermissions.PERMISSIONS.setDefaultGroup(grupo);
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo Inexistente.")));
			}
		}
	}
	
	private void get_default(ICommandSender sender, String[] args) {
		String group = CienPermissions.PERMISSIONS.getDefaultGroup();
		if (group == null) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo padrão não definido")));
		} else {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+group)));
		}
	}
	
	private void set_group(ICommandSender sender, String[] args) {
		if (args.length < 3) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set group <prefix/removed>")));
		} else {
			String arg = args[2].toLowerCase();
			if (arg.equals("prefix")) {
				group_prefix(sender, args);
			} else if (arg.equals("removed")) {
				group_removed(sender, args);
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set group <prefix/removed>")));
			}
		}
	}
	
	private void group_prefix(ICommandSender sender, String[] args) {
		if (args.length < 5) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set group prefix <grupo> <prefix>")));
		} else {
			String group = args[3];
			String prefix = args[4];
			if (CienPermissions.PERMISSIONS.hasGroup(group)) {
				CienPermissions.PERMISSIONS.setGroupPrefix(group, prefix);
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo Inexistente.")));
			}
		}
	}
	
	private void group_removed(ICommandSender sender, String[] args) {
		if (args.length < 4) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set group removed <grupo>")));
		} else {
			String group = args[3];
			if (CienPermissions.PERMISSIONS.hasGroup(group)) {
				CienPermissions.PERMISSIONS.deleteGroup(group);
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo Inexistente.")));
			}
		}
	}

	private void set_perm(ICommandSender sender, String[] args) {
		if (args.length < 5) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set perm <grupo> <permissão> <true/false>")));
		} else {
			String grupo = args[2];
			String perm = args[3];
			boolean value = Boolean.parseBoolean(args[4]);
			if (!CienPermissions.PERMISSIONS.hasGroup(grupo)) {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Grupo criado automaticamente.")));
			}
			CienPermissions.PERMISSIONS.setGroupPermission(grupo, perm, value);
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
		}
	}

	private void set_player(ICommandSender sender, String[] args) {
		if (args.length < 4) {
			sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /perms set player <player> <grupo>")));
		} else {
			String player = args[2];
			String grupo = args[3];
			if (Properties.hasProperties(player)) {
				if (CienPermissions.PERMISSIONS.hasGroup(grupo)) {
					CienPermissions.PERMISSIONS.setGroup(player, grupo);
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Sucesso!")));
				} else {
					sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Grupo Inexistente.")));
				}
			} else {
				sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player Inexistente.")));
			}
		}
	}

}
