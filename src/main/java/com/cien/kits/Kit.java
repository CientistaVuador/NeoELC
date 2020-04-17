package com.cien.kits;

import java.util.ArrayList;
import java.util.List;

import com.cien.Util;
import com.cien.data.Node;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;

import net.minecraft.item.ItemStack;

public final class Kit {
	
	private final String name;
	private final Properties prop;
	private final List<ItemStack> items = new ArrayList<>();
	private long delay;
	private boolean keepNbt;
	
	
	public Kit(String name) {
		this.name = name;
		this.prop = Properties.getProperties("(Kit)"+name);
		this.delay = 0;
		this.keepNbt = true;
		save();
	}
	
	public Kit(Properties prop) {
		this.prop = prop;
		this.name = prop.get("name");
		this.delay = Long.parseLong(prop.get("delay"));
		this.keepNbt = Boolean.parseBoolean(prop.get("keepNbt"));
		Node items = prop.getNode("items");
		for (Node item:items.getNodes()) {
			this.items.add(Util.getItemStackFromNode(item));
		}
	}
	
	private void save() {
		prop.set("name", name);
		prop.set("delay", Long.toString(delay));
		prop.set("keepNbt", Boolean.toString(keepNbt));
		Node items = new Node("items");
		int index = 0;
		for (ItemStack s:getItems()) {
			items.addNode(Util.getNodeFromItemStack(Integer.toString(index), s, keepNbt));
			index++;
		}
		prop.setNode("items", items);
	}
	
	public ItemStack[] getItems() {
		ItemStack[] array = items.toArray(new ItemStack[items.size()]);
		ItemStack[] toReturn = new ItemStack[array.length];
		for (int i = 0; i < array.length; i++) {
			toReturn[i] = array[i].copy();
		}
		return toReturn;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public String getName() {
		return name;
	}
	
	public Properties getProp() {
		return prop;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
		save();
	}
	
	public void setKeepNbt(boolean keepNbt) {
		this.keepNbt = keepNbt;
		save();
	}
	
	public void addItemStack(ItemStack stack) {
		items.add(stack.copy());
		save();
	}
	
	public int getSize() {
		return items.size();
	}
	
	public String getPermission() {
		return "kit."+name;
	}
	
	public boolean hasPermission(String player) {
		return CienPermissions.PERMISSIONS.hasPermission(player, getPermission());
	}
	
	public long getTimeLeftFor(String player) {
		if (Properties.hasProperties(player)) {
			Properties prop = Properties.getProperties(player);
			String left = prop.get("kit."+name+".timeleft");
			if (left == null) {
				return 0;
			}
			long timeLeft = Long.parseLong(left) - System.currentTimeMillis();
			if (timeLeft > delay) {
				prop.set("kit."+name+".timeleft", Long.toString(delay+System.currentTimeMillis()));
				return delay;
			}
			if (timeLeft < 0) {
				timeLeft = 0;
			}
			return timeLeft;
		}
		return 0;
	}
	
	public void resetTimeLeftFor(String player) {
		if (Properties.hasProperties(player)) {
			Properties prop = Properties.getProperties(player);
			prop.set("kit."+name+".timeleft", Long.toString(delay+System.currentTimeMillis()));
		}
	}
	
	public ItemStack getItemStack(int index) {
		return items.get(index).copy();
	}
	
	public void removeItemStack(int index) {
		items.remove(index);
		save();
	}
	
	public boolean isKeepNbt() {
		return keepNbt;
	}
}
