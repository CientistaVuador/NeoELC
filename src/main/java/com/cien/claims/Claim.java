package com.cien.claims;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.data.Node;
import com.cien.data.Properties;

import net.minecraft.entity.player.EntityPlayerMP;

public final class Claim {

	private final PositiveLocation loc1;
	private final PositiveLocation loc2;
	private final String world;
	private final String owner;
	private final List<String> flags = new ArrayList<>();
	private final Properties prop;
	private final int id;
	
	private PositiveLocation center = null;
	
	public Claim(PositiveLocation center, String world, String owner, int id, int width, int lenght) {
		this.world = world;
		this.owner = owner;
		if (id >= 0) {
			this.prop = Properties.getProperties("(Claim)"+id);
		} else {
			this.prop = null;
		}
		this.id = id;
		this.loc1 = center.add(width * -1, 0, lenght);
		this.loc2 = center.add(width, 0, lenght * -1);
		if (id >= 0) {
			save();
		}
	}
	
	public Claim(PositiveLocation loc1, PositiveLocation loc2, String world, String owner, int id) {
		this.loc1 = loc1;
		this.loc2 = loc2;
		this.world = world;
		this.owner = owner;
		if (id >= 0) {
			this.prop = Properties.getProperties("(Claim)"+id);
		} else {
			this.prop = null;
		}
		this.id = id;
		if (id >= 0) {
			save();
		}
	}
	
	public Claim(Properties prop) {
		this.world = prop.get("world");
		this.owner = prop.get("owner");
		this.id = Integer.parseInt(prop.get("id"));
		this.prop = prop;
		
		Node loc1 = prop.getNode("loc1");
		Node loc2 = prop.getNode("loc2");
		
		this.loc1 = new PositiveLocation(
				Integer.parseInt(loc1.getField("x")),
				Integer.parseInt(loc1.getField("y")),
				Integer.parseInt(loc1.getField("z")));
		this.loc2 = new PositiveLocation(
				Integer.parseInt(loc2.getField("x")),
				Integer.parseInt(loc2.getField("y")),
				Integer.parseInt(loc2.getField("z")));
		this.flags.addAll(Arrays.asList(prop.getArray("flags")));
		if (id >= 0) {
			save();
		}
	}
	
	private void save() {
		prop.set("world", world);
		prop.set("owner", owner);
		prop.set("id", Integer.toString(id));
		prop.setArray("flags", flags.toArray(new String[flags.size()]));
		
		Node loc1 = prop.getNode("loc1");
		Node loc2 = prop.getNode("loc2");
		
		loc1.setField("x", Integer.toString(this.loc1.getX()));
		loc1.setField("y", Integer.toString(this.loc1.getY()));
		loc1.setField("z", Integer.toString(this.loc1.getZ()));
		
		loc2.setField("x", Integer.toString(this.loc2.getX()));
		loc2.setField("y", Integer.toString(this.loc2.getY()));
		loc2.setField("z", Integer.toString(this.loc2.getZ()));
		
		prop.setNode("loc1", loc1);
		prop.setNode("loc2", loc2);
	}
	
	public PositiveLocation getLocation1() {
		return loc1;
	}
	
	public PositiveLocation getLocation2() {
		return loc2;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getWorld() {
		return world;
	}

	public int getId() {
		return id;
	}
	
	public String[] getFlags() {
		return flags.toArray(new String[flags.size()]);
	}
	
	public Properties getProperties() {
		return prop;
	}
	
	public boolean getFlag(String flag) {
		flag = flag.toLowerCase();
		for (String s:getFlags()) {
			if (s.equals(flag)) {
				return true;
			}
		}
		return false;
	}
	
	public void setFlag(String flag, boolean value) {
		flag = flag.toLowerCase();
		for (String s:getFlags()) {
			if (s.equals(flag)) {
				if (!value) {
					flags.remove(flag);
				}
				return;
			}
		}
		flags.add(flag);
		save();
	}
	
	public int getWidth() {
		return loc1.distanceX(loc2);
	}
	
	public int getLenght() {
		return loc1.distanceZ(loc2);
	}
	
	public int getSize() {
		return getWidth() * getLenght();
	}
	
	public PositiveLocation getUpperLeftCorner() {
		return getCenter().add(getWidth()/2 * -1, 0, getLenght()/2);
	}
	
	public PositiveLocation getUpperRightCorner() {
		return getCenter().add(getWidth()/2, 0, getLenght()/2);
	}
	
	public PositiveLocation getDownRightCorner() {
		return getCenter().add(getWidth()/2, 0, getLenght()/2 * -1);
	}
	
	public PositiveLocation getDownLeftCorner() {
		return getCenter().add(getWidth()/2 * -1, 0, getLenght()/2 * -1);
	}
	
	public int getHypotenuse() {
		return (int)Math.hypot(getWidth(), getLenght());
	}
	
	public PositiveLocation getCenter() {
		if (center != null) {
			return center;
		}
		center = new PositiveLocation(
				(PositiveLocation.bigger(loc1.getPositiveX(), loc2.getPositiveX())-getWidth())-PositiveLocation.MAX,
				loc1.getY(),
				(PositiveLocation.bigger(loc1.getPositiveZ(), loc2.getPositiveZ())-getLenght())-PositiveLocation.MAX);
		return center;
	}
	
	public int distanceXZ(PositiveLocation point) {
		if (PositiveLocation.insideXZ(loc1, loc2, point)) {
            return 0;
        }
        int x = point.getX();
        int z = point.getZ();
        
        int smallX = PositiveLocation.smaller(loc1.getX(), loc2.getX());
        int biggerX = PositiveLocation.bigger(loc1.getX(), loc2.getX());
        
        int smallZ = PositiveLocation.smaller(loc1.getZ(), loc2.getX());
        int biggerZ = PositiveLocation.bigger(loc1.getZ(), loc2.getZ());
        
        if (x > smallX && x < biggerX) {
            if (z > smallZ) {
                return PositiveLocation.pos(z - biggerZ);
            } else {
                return PositiveLocation.pos(z - smallZ);
            }
        }
        if (z > smallZ && z < biggerZ) {
            if (x > smallX) {
                return PositiveLocation.pos(x - biggerX);
            } else {
                return PositiveLocation.pos(x - smallX);
            }
        }
        boolean right = false;
        boolean up = false;
        if (z > biggerZ) {
            up = true;
        }
        if (x > biggerX) {
            right = true;
        }
        int x1;
        int z1;
        if (up) {
            z1 = biggerZ;
        } else {
            z1 = smallZ;
        }
        if (right) {
            x1 = biggerX;
        } else {
            x1 = smallX;
        }
        PositiveLocation p = new PositiveLocation(x1, 0, z1);
        return new Claim(p, point, null, null, -1).getHypotenuse();
	}
	
	public boolean isInside(EntityPlayerMP player) {
		return PositiveLocation.insideXZ(loc1, loc2, new PositiveLocation((int)player.posX, 0, (int)player.posZ));
	}
	
	public boolean isInside(String player) {
		EntityPlayerMP p = Util.getOnlinePlayer(player);
		if (p == null) {
			return false;
		}
		return isInside(p);
	}
}
