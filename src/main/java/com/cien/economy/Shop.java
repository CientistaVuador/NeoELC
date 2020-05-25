package com.cien.economy;

import java.util.ArrayList;
import java.util.List;
import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.claims.Claim;
import com.cien.data.Node;
import com.cien.data.Properties;

import net.minecraft.world.WorldServer;

public class Shop {

	private final String owner;
	private final Properties prop;
	private final List<ChestShop> shops = new ArrayList<>();
	
	private String name;
	private float x;
	private float y;
	private float z;
	private float pitch;
	private float yaw;
	private String world;
	private int id = -1;
	
	
	public Shop(String name, String owner, float x, float y, float z, String world, float pitch, float yaw) {
		this.name = name;
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
		this.prop = Properties.getProperties("(Shop)"+owner);
		this.prop.set("id", Integer.toString(CienEconomy.ECONOMY.nextID()));
		save();
	}
	
	public Shop(Properties prop) {
		this.prop = prop;
		
		this.name = prop.get("name");
		this.owner = prop.get("owner");
		this.x = Float.parseFloat(prop.get("x"));
		this.y = Float.parseFloat(prop.get("y"));
		this.z = Float.parseFloat(prop.get("z"));
		this.pitch = Float.parseFloat(prop.get("pitch"));
		this.yaw = Float.parseFloat(prop.get("yaw"));
		this.world = prop.get("world");
		Node shops = prop.getNode("shops");
		CienEconomy.ECONOMY.run(() -> {
			for (Node chest:shops.getNodes()) {
				try {
					ChestShop s = new ChestShop(chest);
					if (s.isValid()) {
						this.shops.add(s);
						s.shop = this;
					} else {
						System.out.println("Uma loja de "+owner+" é inválida e foi removida.");
					}
				} catch (Exception ex) {
					System.out.println("Erro ao carregar ChestShop de "+owner+": "+ex.getMessage());
				}
			}
		});
	}
	
	public int getID() {
		if (this.id == -1) {
			this.id = Integer.parseInt(prop.get("id"));
		}
		return this.id;
	}
	
	private void save() {
		prop.set("name", this.name);
		prop.set("x", Float.toString(this.x));
		prop.set("y", Float.toString(this.y));
		prop.set("z", Float.toString(this.z));
		prop.set("pitch", Float.toString(this.pitch));
		prop.set("yaw", Float.toString(this.yaw));
		prop.set("owner", this.owner);
		prop.set("world", this.world);
		Node shops = new Node("shops");
		ChestShop[] chests = getChestShops();
		for (int i = 0; i < chests.length; i++) {
			ChestShop f = chests[i];
			Node g = f.toNode();
			g.setName(Integer.toString(i));
			shops.addNode(g);
		}
		prop.setNode("shops", shops);
	}
	
	public ChestShop getChestShop(String world, int x, int y, int z) {
		for (ChestShop s:getChestShops()) {
			if (!s.isValid()) {
				removeChestShop(s);
				continue;
			}
			if (s.getWorld().equals(world)) {
				if (s.getX() == x) {
					if (s.getZ() == z) {
						if (s.getY() == y || (s.getY()+1) == y) {
							return s;
						}
					}
				}
			}
		}
		return null;
	}
	
	public ChestShop[] getChestShops() {
		return shops.toArray(new ChestShop[shops.size()]);
	}
	
	public void addChestShop(ChestShop f) {
		shops.add(f);
		f.shop = this;
		save();
	}
	
	public boolean removeChestShop(ChestShop g) {
		boolean b = shops.remove(g);
		if (b) {
			save();
			g.shop = null;
		}
		return b;
	}
	
	public void disable() {
		this.name = null;
		save();
	}
	
	public boolean isValid() {
		WorldServer world = Util.getWorld(this.world);
		if (world == null) {
			return false;
		}
		Claim f = CienClaims.CLAIMS.getClaimInside(new PositiveLocation((int)this.x, (int)this.y, (int)this.z), world);
		if (f == null) {
			return false;
		}
		if (!f.getOwner().equals(this.owner)) {
			return false;
		}
		return true;
	}
	
	public boolean isValidFor(String player) {
		WorldServer world = Util.getWorld(this.world);
		if (world == null) {
			return false;
		}
		Claim f = CienClaims.CLAIMS.getClaimInside(new PositiveLocation((int)this.x, (int)this.y, (int)this.z), world);
		if (f == null) {
			return false;
		}
		if (!f.getOwner().equals(this.owner)) {
			return false;
		}
		if (!f.getOwner().equals(player)) {
			if (!f.getFlag("permitirEntrar#*") && !f.getFlag("permitirEntrar#"+player)) {
				return false;
			}
		}
		return true;
	}
	
	public void enable(String name, float x, float y, float z, float pitch, float yaw, String world) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
		save();
	}
	
	public boolean isEnabled() {
		return this.name != null;
	}
	
	public void setName(String name) {
		this.name = name;
		save();
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
		save();
	}
	
	public void setWorld(String world) {
		this.world = world;
		save();
	}
	
	public void setX(float x) {
		this.x = x;
		save();
	}
	
	public void setY(float y) {
		this.y = y;
		save();
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
		save();
	}
	
	public void setZ(float z) {
		this.z = z;
		save();
	}
	
	public String getName() {
		return name;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public Properties getProp() {
		return prop;
	}
	
	public String getWorld() {
		return world;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getZ() {
		return z;
	}
	
	

}
