package com.cien.command;

import java.util.ArrayList;
import java.util.List;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class CienCommand extends CienCommandBase implements CienCommandInterface {

	private final boolean permission;
	private final boolean playerOnly;
	private final List<CienSubCommand> subs = new ArrayList<>();
	private final ArgumentType[] argsTypes;
	private String usageCache = null;
	
	public CienCommand(String name, String description, boolean permission, boolean playerOnly, ArgumentType... args) {
		super(name, description);
		this.permission = permission;
		this.playerOnly = playerOnly;
		this.argsTypes = args;
		if (argsTypes[0] == ArgumentType.TYPE_SUBCOMMAND) {
			if (argsTypes.length > 1) {
				throw new RuntimeException("Invalid Command, cannot have more than one arg in subcommand type arg, "+getCommandName());
			}
		} else {
			for (ArgumentType type:argsTypes) {
				if (type == ArgumentType.TYPE_SUBCOMMAND) {
					throw new RuntimeException("Invalid Command, cannot have more than one arg in subcommand type arg, "+getCommandName());
				}
			}
		}
	}
	
	public boolean isPlayerOnly() {
		return playerOnly;
	}
	
	public boolean hasPermission() {
		return permission;
	}
	
	public void addSubCommand(CienSubCommand cmd) {
		usageCache = null;
		for (CienSubCommand s:getSubCommands()) {
			if (s.getName().equals(cmd.getName())) {
				throw new RuntimeException("Command already contains "+cmd.getName());
			}
		}
		subs.add(cmd);
	}
	
	public boolean removeSubCommand(CienSubCommand cmd) {
		usageCache = null;
		return subs.remove(cmd);
	}
	
	public CienSubCommand[] getSubCommands() {
		return subs.toArray(new CienSubCommand[subs.size()]);
	}
	
	public String getUsage() {
		if (usageCache != null) {
			return usageCache;
		}
		StringBuilder b = new StringBuilder(64);
		if (argsTypes.length == 0) {
			usageCache = "Uso: /"+getCommandName();
			return usageCache;
		}
		if (argsTypes.length == 1 && argsTypes[0] == ArgumentType.TYPE_SUBCOMMAND) {
			b.append("Uso: /");
			b.append(getCommandName());
			b.append('<');
			CienSubCommand[] sub = getSubCommands();
			for (int i = 0; i < sub.length; i++) {
				b.append(sub[i].getName());
				if (i != (sub.length - 1)) {
					b.append('/');
				}
			}
			b.append('>');
			usageCache = b.toString();
			return usageCache;
		}
		b.append("Uso: /");
		b.append(getCommandName());
		for (int i = 0; i < argsTypes.length; i++) {
			b.append('<');
			b.append(argsTypes[i].getName());
			b.append('>');
			if (i != (argsTypes.length - 1)) {
				b.append(' ');
			}
		}
		usageCache = b.toString();
		return usageCache;
	}
	
	@Override
	public String getName() {
		return getCommandName();
	}
	
	public CienSubCommand getSubCommand(String name) {
		name = name.toLowerCase();
		CienSubCommand[] cmds = getSubCommands();
		for (CienSubCommand s:cmds) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		
		for (CienSubCommand s:cmds) {
			if (s.getName().startsWith(name)) {
				return s;
			}
		}
		
		for (CienSubCommand s:cmds) {
			if (s.getName().contains(name)) {
				return s;
			}
		}
		
		return null;
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (player == null && playerOnly) {
			Util.sendMessage(sender, Util.getErrorPrefix()+"Só pode ser usado por jogadores.");
			return;
		}
		if (permission && (sender instanceof EntityPlayerMP) && !CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), getPermission())) {
			Util.sendMessage(sender, Util.getErrorPrefix()+"Sem Permissão.");
		} else {
			if (argsTypes.length == 0) {
				onExecute(sender, args);
			} else {
				if (argsTypes[0] == ArgumentType.TYPE_SUBCOMMAND) {
					if (args.length == 0) {
						Util.sendMessage(sender, Util.getErrorPrefix()+getUsage());
					} else {
						CienSubCommand sub = getSubCommand(args[0]);
						if (sub == null) {
							Util.sendMessage(sender, Util.getErrorPrefix()+getUsage());
							return;
						}
						sub.onCommand(sender, args);
					}
				} else {
					if (args.length != argsTypes.length) {
						Util.sendMessage(sender, Util.getErrorPrefix()+getUsage());
						return;
					}
					Object[] s = new Object[args.length];
					for (int i = 0; i < args.length; i++) {
						s[i] = ArgumentType.parse(args[i], argsTypes[i]);
						if (s[i] == null) {
							Util.sendMessage(sender, Util.getErrorPrefix()+args[0]+" não é um "+argsTypes[i].getName()+" válido.");
							return;
						}
					}
					onExecute(sender, s);
				}
			}
		}
	}
	
	public String getPermission() {
		return "admin."+getCommandName();
	}
	
	public abstract void onExecute(ICommandSender sender, Object[] args);
}
