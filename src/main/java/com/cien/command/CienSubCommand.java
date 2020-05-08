package com.cien.command;

import java.util.ArrayList;
import java.util.List;

import com.cien.Util;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class CienSubCommand implements CienCommandInterface {
	
	private final String name;
	private final ArgumentType[] argsTypes;
	private final boolean permission;
	private final boolean playerOnly;
	private String permissionCache = null;
	private String usageCache = null;
	private CienCommandInterface[] parentsCache = null;
	private CienCommandInterface[] parentsCacheReversed = null;
	private final List<CienSubCommand> subs = new ArrayList<>();
	CienCommandInterface parent;
	
	public CienSubCommand(String name, boolean permission, boolean playerOnly, ArgumentType... args) {
		this.argsTypes = args;
		this.name = name.toLowerCase();
		if (this.name.contains(" ")) {
			throw new RuntimeException("No Space allowed in subcommand "+name);
		}
		this.permission = permission;
		this.playerOnly = playerOnly;
		if (argsTypes[0] == ArgumentType.TYPE_SUBCOMMAND) {
			if (argsTypes.length > 1) {
				throw new RuntimeException("Invalid Command, cannot have more than one arg in subcommand type arg, "+getName());
			}
		} else {
			for (ArgumentType type:argsTypes) {
				if (type == ArgumentType.TYPE_SUBCOMMAND) {
					throw new RuntimeException("Invalid Command, cannot have more than one arg in subcommand type arg, "+getName());
				}
			}
		}
	}
	
	@Override
	public CienCommandInterface getParent() {
		return parent;
	}
	
	public CienCommandInterface[] getParents() {
		if (parentsCache != null) {
			return parentsCache.clone();
		}
		List<CienCommandInterface> ins = new ArrayList<>();
		CienCommandInterface f = this;
		while ((f = f.getParent()) != null) {
			ins.add(f);
		}
		CienCommandInterface[] rev = new CienCommandInterface[ins.size()];
		parentsCache = rev;
		return parentsCache;
	}
	
	public CienCommandInterface[] getParentsReversed() {
		if (parentsCacheReversed != null) {
			return parentsCacheReversed.clone();
		}
		CienCommandInterface[] pr = getParents();
		CienCommandInterface[] rev = new CienCommandInterface[pr.length];
		for (int i = 0; i < rev.length; i++) {
			rev[i] = pr[(rev.length - 1) - i];
		}
		parentsCacheReversed = rev;
		return parentsCacheReversed;
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
	
	public String getPermission() {
		if (permissionCache != null) {
			return permissionCache;
		}
		StringBuilder b = new StringBuilder(64);
		b.append("admin.");
		for (CienCommandInterface f:getParents()) {
			b.append(f.getName());
			b.append('.');
		}
		b.setLength(b.length() - 1);
		permissionCache = b.toString();
		return permissionCache;
	}
	
	public String getUsage() {
		if (usageCache != null) {
			return usageCache;
		}
		StringBuilder b = new StringBuilder(64);
		CienCommandInterface[] parents = getParentsReversed();
		for (int i = 0; i < parents.length; i++) {
			b.append(parents[i].getName());
			if (i != (parents.length - 1)) {
				b.append(' ');
			}
		}
		if (argsTypes.length == 0) {
			usageCache = b.toString();
			return usageCache;
		}
		if (argsTypes.length == 1 && argsTypes[0] == ArgumentType.TYPE_SUBCOMMAND) {
			b.append(' ');
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
		for (int i = 0; i < argsTypes.length; i++) {
			b.append(' ');
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
	
	public int getParentLevel() {
		return getParents().length;
	}
	
	public String getName() {
		return name;
	}
	
	public void onCommand(ICommandSender sender, String[] args) {
		if (!(sender instanceof EntityPlayerMP) && playerOnly) {
			Util.sendMessage(sender, Util.getErrorPrefix()+"Só pode ser usado por jogadores.");
			return;
		}
		String[] f = new String[args.length - getParentLevel()];
		for (int i = 0; i < f.length; i++) {
			f[i] = args[i + getParentLevel()];
		}
		args = f;
		if (permission && (sender instanceof EntityPlayerMP) && !CienPermissions.PERMISSIONS.hasPermission(sender.getCommandSenderName(), getPermission())) {
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
	
	public abstract void onExecute(ICommandSender sender, Object[] args);
}
