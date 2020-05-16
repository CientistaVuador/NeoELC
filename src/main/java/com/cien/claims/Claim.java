package com.cien.claims;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.data.Node;
import com.cien.data.Properties;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public final class Claim {

	public static final int NONE = -1;
	public static final int ENTRY = 0;
	public static final int TRUST = 1;
	public static final int CUSTOM = 2;
	
	
	private final int biggerX;
	private final int smallerX;
	private final int biggerZ;
	private final int smallerZ;
	
	private final PositiveLocation left_up;
	private final PositiveLocation left_down;
	private final PositiveLocation right_up;
	private final PositiveLocation right_down;
	private final PositiveLocation center;
	
	private final int lenght;
	private final int width;
	
	private final String world;
	private String owner;
	private List<String> flags = new ArrayList<>();
	private final Properties prop;
	private final int id;
	
	private final Claim shield;
	
	public Claim(PositiveLocation center, String world, String owner, int id, int width, int lenght) {
		this.world = world;
		this.owner = owner;
		if (id >= 0) {
			this.prop = Properties.getProperties("(Claim)"+id);
		} else {
			this.prop = null;
		}
		this.id = id;
		
		left_up = center.add(
				(width/2) * -1,
				0,
				lenght / 2
				);
		
		left_down = center.add(
				(width/2) * -1,
				0,
				(lenght/2) * -1
				);
		
		right_up = center.add(
				width / 2,
				0,
				lenght / 2
				);
		
		right_down = center.add(
				width / 2,
				0,
				(lenght/2) * -1
				);
		
		this.center = center;
		
		this.biggerX = right_up.getX();
		this.smallerX = left_up.getX();
		this.biggerZ = left_up.getZ();
		this.smallerZ = left_down.getZ();
		
		this.width = width;
		this.lenght = lenght;
		
		if (id >= 0) {
			this.shield = new Claim(left_up.add(-50, 0, 50), right_down.add(50, 0, -50), world, owner, -1);
			this.shield.flags = this.flags;
		} else {
			this.shield = null;
		}
	}
	
	public Claim(PositiveLocation loc1, PositiveLocation loc2, String world, String owner, int id) {
		
		this.biggerX = PositiveLocation.bigger(loc1.getX(), loc2.getX());
		this.smallerX = PositiveLocation.smaller(loc1.getX(), loc2.getX());
		this.biggerZ = PositiveLocation.bigger(loc1.getZ(), loc2.getZ());
		this.smallerZ = PositiveLocation.smaller(loc1.getZ(), loc2.getZ());
		
		int y = loc1.getY();
		
		left_up = new PositiveLocation(
				smallerX,
				y,
				biggerZ
				);
		
		left_down = new PositiveLocation(
				smallerX,
				y,
				smallerZ
				);
		
		right_up = new PositiveLocation(
				biggerX,
				y,
				biggerZ
				);
		
		right_down = new PositiveLocation(
				biggerX,
				y,
				smallerZ
				);
		
		center = new PositiveLocation(
				biggerX/2,
				y,
				biggerZ/2
				);
		
		this.width = right_down.getPositiveX() - left_down.getPositiveX();
		this.lenght = left_up.getPositiveZ() - left_down.getPositiveZ();
		
		this.world = world;
		this.owner = owner;
		if (id >= 0) {
			this.prop = Properties.getProperties("(Claim)"+id);
		} else {
			this.prop = null;
		}
		this.id = id;
		
		if (id >= 0) {
			this.shield = new Claim(left_up.add(-50, 0, 50), right_down.add(50, 0, -50), world, owner, -1);
			this.shield.flags = this.flags;
		} else {
			this.shield = null;
		}
	}
	
	public Claim(Properties prop) {
		this.world = prop.get("world");
		this.owner = prop.get("owner");
		this.id = Integer.parseInt(prop.get("id"));
		this.prop = prop;
		
		Node pos1 = prop.getNode("loc1");
		Node pos2 = prop.getNode("loc2");
		
		PositiveLocation loc1 = new PositiveLocation(
				Integer.parseInt(pos1.getField("x")),
				Integer.parseInt(pos1.getField("y")),
				Integer.parseInt(pos1.getField("z")));
		PositiveLocation loc2 = new PositiveLocation(
				Integer.parseInt(pos2.getField("x")),
				Integer.parseInt(pos2.getField("y")),
				Integer.parseInt(pos2.getField("z")));
		
		this.biggerX = PositiveLocation.bigger(loc1.getX(), loc2.getX());
		this.smallerX = PositiveLocation.smaller(loc1.getX(), loc2.getX());
		this.biggerZ = PositiveLocation.bigger(loc1.getZ(), loc2.getZ());
		this.smallerZ = PositiveLocation.smaller(loc1.getZ(), loc2.getZ());
		
		int y = loc1.getY();
		
		left_up = new PositiveLocation(
				smallerX,
				y,
				biggerZ
				);
		
		left_down = new PositiveLocation(
				smallerX,
				y,
				smallerZ
				);
		
		right_up = new PositiveLocation(
				biggerX,
				y,
				biggerZ
				);
		
		right_down = new PositiveLocation(
				biggerX,
				y,
				smallerZ
				);
		
		center = new PositiveLocation(
				biggerX/2,
				y,
				biggerZ/2
				);
		
		this.width = right_down.getPositiveX() - left_down.getPositiveX();
		this.lenght = left_up.getPositiveZ() - left_down.getPositiveZ();
		
		this.flags.addAll(Arrays.asList(prop.getArray("flags")));
		
		if (id >= 0) {
			this.shield = new Claim(left_up.add(-50, 0, 50), right_down.add(50, 0, -50), world, owner, -1);
			this.shield.flags = this.flags;
		} else {
			this.shield = null;
		}
	}
	
	public Claim expand(int size) {
		Claim c = new Claim(
				getUpperLeftCorner().add(size * -1, 0, size),
				getDownRightCorner().add(size, 0, size * -1),
				world,
				owner,
				id
				);
		c.flags.addAll(this.flags);
		return c;
	}
	
	public void removeAllFlagsWith(String player) {
		for (String flag:getFlags()) {
			String[] e = getPermissionAndPlayer(flag);
			if (e == null) {
				continue;
			}
			if (e[1].equalsIgnoreCase(player)) {
				setFlag(flag, false);
			}
		}
	}
	
	public EntityPlayerMP[] getPlayersInside() {
		List<EntityPlayerMP> list = new ArrayList<>();
		for (EntityPlayerMP online:Util.getOnlinePlayers()) {
			if (isInside(online)) {
				list.add(online);
			}
		}
		return list.toArray(new EntityPlayerMP[list.size()]);
	}
	
	public Entity[] getEntities() {
		WorldServer world = Util.getWorld(this.world);
		if (world == null) {
			return null;
		}
		List<?> entitiesList = world.loadedEntityList;
		List<Entity> list = new ArrayList<>();
		List<Entity> filter = new ArrayList<>();
		for (Object o:entitiesList) {
			if (o instanceof Entity) {
				if (!(o instanceof EntityPlayerMP)) {
					list.add((Entity)o);
				}
			}
		}
		for (Entity o:list.toArray(new Entity[list.size()])) {
			if (!o.worldObj.provider.getDimensionName().equals(this.world)) {
				continue;
			}
			PositiveLocation loc = new PositiveLocation((int)o.posX, (int)o.posY, (int)o.posZ);
			if (isInside(loc)) {
				filter.add(o);
			}
		}
		return filter.toArray(new Entity[filter.size()]);
	}
	
	private void save() {
		prop.set("world", world);
		prop.set("owner", owner);
		prop.set("id", Integer.toString(id));
		prop.setArray("flags", flags.toArray(new String[flags.size()]));
		
		Node loc1 = prop.getNode("loc1");
		Node loc2 = prop.getNode("loc2");
		
		loc1.setField("x", Integer.toString(this.left_up.getX()));
		loc1.setField("y", Integer.toString(this.left_up.getY()));
		loc1.setField("z", Integer.toString(this.left_up.getZ()));
		
		loc2.setField("x", Integer.toString(this.right_down.getX()));
		loc2.setField("y", Integer.toString(this.right_down.getY()));
		loc2.setField("z", Integer.toString(this.right_down.getZ()));
		
		prop.setNode("loc1", loc1);
		prop.setNode("loc2", loc2);
	}
	
	public void setOwner(String owner) {
		prop.set("owner", owner);
		this.owner = owner;
	}
	
	public Map<String, Integer> getPlayersAndPermissionLevel() {
		Map<String, Integer> map = new HashMap<>();
		for (String p:getPlayersWithPermissions()) {
			map.put(p, getPlayerPermissionLevel(p));
		}
		return map;
	}
	
	public int getPlayerPermissionLevel(String player) {
		int level = NONE;
		for (String s:getFlags()) {
			String[] g = getPermissionAndPlayer(s);
			if (g == null) {
				continue;
			}
			if (!g[1].equalsIgnoreCase(player)) {
				continue;
			}
			if (g[0].equalsIgnoreCase("permitirUsarBloco")) {
				if (level == NONE || level == ENTRY) {
					level = TRUST;
				}
			}
			if (g[0].equalsIgnoreCase("permitirEntrar")) {
				if (level == NONE) {
					level = ENTRY;
				}
			}
			if (g[0].equalsIgnoreCase("permitirUsarItem")) {
				if (level == NONE || level == ENTRY) {
					level = TRUST;
				}
			}
			if (g[0].equalsIgnoreCase("permitirColocar")) {
				if (level == NONE || level == ENTRY) {
					level = TRUST;
				}
			}
			if (g[0].equalsIgnoreCase("permitirQuebrar")) {
				if (level == NONE || level == ENTRY) {
					level = TRUST;
				}
			}
		}
		return level;
	}
	
	public String[] getPlayersWithPermissions() {
		List<String> players = new ArrayList<>();
		for (String s:getFlags()) {
			String[] g = getPermissionAndPlayer(s);
			if (g == null) {
				continue;
			}
			boolean allow = false;
			if (g[0].equalsIgnoreCase("permitirUsarBloco")) {
				allow = true;
			}
			if (g[0].equalsIgnoreCase("permitirEntrar")) {
				allow = true;
			}
			if (g[0].equalsIgnoreCase("permitirUsarItem")) {
				allow = true;
			}
			if (g[0].equalsIgnoreCase("permitirColocar")) {
				allow = true;
			}
			if (g[0].equalsIgnoreCase("permitirQuebrar")) {
				allow = true;
			}
			if (allow) {
				if (!(players.contains(g[1]))) {
					players.add(g[1]);
				}
			}
		}
		return players.toArray(new String[players.size()]);
	}
	
	private String[] getPermissionAndPlayer(String s) {
		StringBuilder b = new StringBuilder(64);
		String[] toReturn = new String[2];
		boolean divide = false;
		for (char c:s.toCharArray()) {
			if (c == '#' && !divide) {
				divide = true;
				toReturn[0] = b.toString();
				b.setLength(0);
				continue;
			}
			b.append(c);
		}
		toReturn[1] = b.toString();
		if (!divide) {
			return null;
		}
		return toReturn;
	}
	
	public boolean collidesWith(Claim other) {
		if (other == null) {
			return false;
		}
		if (!other.getWorld().equals(getWorld())) {
			return false;
		}
		return isInside(other.getDownLeftCorner()) ||
				isInside(other.getDownRightCorner()) ||
				isInside(other.getUpperLeftCorner()) ||
				isInside(other.getUpperRightCorner());
	}
	
	public void makeFencesAndSave() {
		WorldServer w = Util.getWorld(getWorld());
		for (int x = smallerX; x < biggerX; x++) {
            int highY = Util.getHighestYAt(x, smallerZ, w) + 1;
            w.setBlock(x, highY, smallerZ, Block.getBlockById(85));
        }
        for (int x = smallerX; x < biggerX; x++) {
        	int highY = Util.getHighestYAt(x, biggerZ, w) + 1;
            w.setBlock(x, highY, biggerZ, Block.getBlockById(85));
        }
        for (int z = smallerZ; z < biggerZ; z++) {
        	int highY = Util.getHighestYAt(smallerX, z, w) + 1;
            w.setBlock(smallerX, highY, z, Block.getBlockById(85));
        }
        for (int z = smallerZ; z < biggerZ; z++) {
        	int highY = Util.getHighestYAt(biggerX, z, w) + 1;
            w.setBlock(biggerX, highY, z, Block.getBlockById(85));
        }
        int highY = Util.getHighestYAt(biggerX, biggerZ, w) + 1;
        w.setBlock(biggerX, highY, biggerZ, Block.getBlockById(85));
        highY = Util.getHighestYAt(smallerX, smallerZ, w) + 1;
        w.setBlock(smallerX, highY, smallerZ, Block.getBlockById(85));
        save();
	}
	
	public void undoFences() {
		WorldServer w = Util.getWorld(getWorld());
		for (int x = smallerX; x < biggerX; x++) {
            int highY = Util.getHighestYAt(x, smallerZ, w);
            if (Block.getIdFromBlock(w.getBlock(x, highY, smallerZ)) == 85) {
            	w.setBlock(x, highY, smallerZ, Block.getBlockById(0));
            }
        }
        for (int x = smallerX; x < biggerX; x++) {
        	int highY = Util.getHighestYAt(x, biggerZ, w);
            if (Block.getIdFromBlock(w.getBlock(x, highY, biggerZ)) == 85) {
            	w.setBlock(x, highY, biggerZ, Block.getBlockById(0));
            }
        }
        for (int z = smallerZ; z < biggerZ; z++) {
        	int highY = Util.getHighestYAt(smallerX, z, w);
            if (Block.getIdFromBlock(w.getBlock(smallerX, highY, z)) == 85) {
            	w.setBlock(smallerX, highY, z, Block.getBlockById(0));
            }
        }
        for (int z = smallerZ; z < biggerZ; z++) {
        	int highY = Util.getHighestYAt(biggerX, z, w);
            if (Block.getIdFromBlock(w.getBlock(biggerX, highY, z)) == 85) {
            	w.setBlock(biggerX, highY, z, Block.getBlockById(0));
            }
        }
        int highY = Util.getHighestYAt(biggerX, biggerZ, w);
        if (Block.getIdFromBlock(w.getBlock(biggerX, highY, biggerZ)) == 85) {
        	w.setBlock(biggerX, highY, biggerZ, Block.getBlockById(0));
        }
        highY = Util.getHighestYAt(smallerX, smallerZ, w);
        if (Block.getIdFromBlock(w.getBlock(smallerX, highY, smallerZ)) == 85) {
        	w.setBlock(smallerX, highY, smallerZ, Block.getBlockById(0));
        }
	}
	
	public PositiveLocation getLocation1() {
		return left_up;
	}
	
	public PositiveLocation getLocation2() {
		return right_down;
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
	
	public Claim getShield() {
		return shield;
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
					save();
				}
				return;
			}
		}
		flags.add(flag);
		save();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getLenght() {
		return lenght;
	}
	
	public int getSize() {
		return getWidth() * getLenght();
	}
	
	public PositiveLocation getUpperLeftCorner() {
		return left_up;
	}
	
	public PositiveLocation getUpperRightCorner() {
		return right_up;
	}
	
	public PositiveLocation getDownRightCorner() {
		return right_down;
	}
	
	public PositiveLocation getDownLeftCorner() {
		return left_down;
	}
	
	public int getHypotenuse() {
		return (int)Math.hypot(getWidth(), getLenght());
	}
	
	public PositiveLocation getCenter() {
		return center;
	}
	
	public int getBiggerX() {
		return biggerX;
	}
	
	public int getBiggerZ() {
		return biggerZ;
	}
	
	public int getSmallerX() {
		return smallerX;
	}
	
	public int getSmallerZ() {
		return smallerZ;
	}
	
	public int distanceXZ(PositiveLocation point) {
		if (PositiveLocation.insideXZ(left_up, right_down, point)) {
            return 0;
        }
        int x = point.getX();
        int z = point.getZ();
        
        
        if (x > smallerX && x < biggerX) {
            if (z > smallerZ) {
                return PositiveLocation.pos(z - biggerZ);
            } else {
                return PositiveLocation.pos(z - smallerZ);
            }
        }
        if (z > smallerZ && z < biggerZ) {
            if (x > smallerX) {
                return PositiveLocation.pos(x - biggerX);
            } else {
                return PositiveLocation.pos(x - smallerX);
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
            z1 = smallerZ;
        }
        if (right) {
            x1 = biggerX;
        } else {
            x1 = smallerX;
        }
        PositiveLocation p = new PositiveLocation(x1, 0, z1);
        return new Claim(p, point, null, null, -1).getHypotenuse();
	}
	
	public boolean isInside(EntityPlayerMP player) {
		if (!player.worldObj.provider.getDimensionName().equals(world)) {
			return false;
		}
		return PositiveLocation.insideXZ(left_up, right_down, new PositiveLocation((int)player.posX, 0, (int)player.posZ));
	}
	
	public boolean isInside(PositiveLocation loc) {
		return PositiveLocation.insideXZ(left_up, right_down, loc);
	}
	
	public boolean isInside(String player) {
		EntityPlayerMP p = Util.getOnlinePlayer(player);
		if (p == null) {
			return false;
		}
		return isInside(p);
	}
}
