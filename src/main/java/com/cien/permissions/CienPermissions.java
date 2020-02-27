package com.cien.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import com.cien.data.Properties;

public class CienPermissions {
	
	public static final CienPermissions PERMISSIONS = new CienPermissions();
	
	Properties data = Properties.getProperties("(Module)CienPermissions");
	
	public CienPermissions() {
		System.out.println("CienPermissions Iniciado!");
	}
	
	//Groups
	public String[] getPermissionsGroups() {
		List<String> groups = new ArrayList<>();
		for (String s:Properties.getAllProperties()) {
			if (s.startsWith("(PermissionsGroup)")) {
				groups.add(s.substring("(PermissionsGroup)".length()));
			}
		}
		return groups.toArray(new String[groups.size()]);
	}
	
	public void setGroupPermission(String group, String permission, boolean value) {
		Properties prop = Properties.getProperties("(PermissionsGroup)"+group);
		prop.setBoolean(permission, value);
	}
	
	public boolean getGroupPermission(String group, String permission) {
		Properties prop = Properties.getProperties("(PermissionsGroup)"+group);
		return prop.getBoolean(permission);
	}
	
	public String[] getGroupPermissions(String group) {
		Properties prop = Properties.getProperties("(PermissionsGroup)"+group);
		List<String> perms = new ArrayList<>();
		for (Entry<String, String> e:prop.getEntries()) {
			if (e.getValue().equals("true")) {
				perms.add(e.getKey());
			}
		}
		return perms.toArray(new String[perms.size()]);
	}
	
	public boolean hasGroup(String group) {
		return Properties.hasProperties("(PermissionsGroup)"+group);
	}
	
	public void reload() {
		String[] groups = getPermissionsGroups();
		for (String s:groups) {
			Properties p = Properties.getProperties("(PermissionsGroup)"+s);
			p.reload();
		}
	}
	
	public void reload(String group) {
		Properties p = Properties.getProperties("(PermissionsGroup)"+group);
		p.reload();
	}
	
	public void setGroupPrefix(String group, String prefix) {
		Properties prop = Properties.getProperties("(PermissionsGroup)"+group);
		prop.set("prefix", prefix);
	}
	
	public String getGroupPrefix(String group) {
		Properties prop = Properties.getProperties("(PermissionsGroup)"+group);
		return prop.get("prefix");
	}
	
	public void setDefaultGroup(String group) {
		data.set("default", group);
	}
	
	public String getDefaultGroup() {
		return data.get("default");
	}
	
	public boolean deleteGroup(String group) {
		Properties prop = Properties.getProperties("(PermissionsGroup)"+group);
		return prop.delete();
	}
	
	//Player
	public void setGroup(String player, String group) {
		Properties prop = Properties.getProperties(player);
		prop.set("permissionsGroup", group);
	}
	
	public String getGroup(String player) {
		if (!Properties.hasProperties(player)) {
			return getDefaultGroup();
		}
		Properties prop = Properties.getProperties(player);
		String group = prop.get("permissionsGroup");
		if (group == null) {
			return getDefaultGroup();
		}
		return group;
	}
	
	public boolean hasPermission(String player, String permission) {
		String playerGroup = getGroup(player);
		return getGroupPermission(playerGroup, permission);
	}
	
	public String getGroupPrefixOf(String player) {
		String gp = getGroup(player);
		return getGroupPrefix(gp);
	}
}
