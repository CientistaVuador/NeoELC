package com.cien.teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import com.cien.Module;
import com.cien.data.Properties;
import cpw.mods.fml.common.event.FMLServerStartingEvent;


public class CienTeleport extends Module {
	
	public static final CienTeleport TELEPORT = new CienTeleport();
	public static final int DEFAULT_MAX_HOMES = 3;
	
	private static String escape(String s) {
		StringBuilder b = new StringBuilder();
		for (char c:s.toCharArray()) {
			switch (c) {
			case ':':
			case '\\':
				b.append('\\');
			}
			b.append(c);
		}
		return b.toString();
	}
	
	
	private final Properties prop = Properties.getProperties("(Module)CienTeleport");
	private final List<Home> homes = new ArrayList<>();
	private final List<Warp> warps = new ArrayList<>();
	
	private CienTeleport() {
		super("CienTeleport");
	}
	
	@Override
	public void start() {
		load();
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new com.cien.teleport.commands.DelHome());
    	event.registerServerCommand(new com.cien.teleport.commands.DelWarp());
    	event.registerServerCommand(new com.cien.teleport.commands.GotoHome());
    	event.registerServerCommand(new com.cien.teleport.commands.Home());
    	event.registerServerCommand(new com.cien.teleport.commands.SetHome());
    	event.registerServerCommand(new com.cien.teleport.commands.SetMaxHomes());
    	event.registerServerCommand(new com.cien.teleport.commands.SetWarp());
    	event.registerServerCommand(new com.cien.teleport.commands.Warp());
    	event.registerServerCommand(new com.cien.teleport.commands.Tpa());
    	event.registerServerCommand(new com.cien.teleport.commands.Tprc());
    	event.registerServerCommand(new com.cien.teleport.commands.Tpac());
    	event.registerServerCommand(new com.cien.teleport.commands.Tphere());
    	event.registerServerCommand(new com.cien.teleport.commands.Tpp());
    	event.registerServerCommand(new com.cien.teleport.commands.Rtp());
	}
	
	public void load() {
		Function<String, String[]> split = (String s) -> {
			StringBuilder b = new StringBuilder(64);
			boolean escape = false;
			List<String> strings = new ArrayList<>();
			for (char c:s.toCharArray()) {
				if (escape) {
					escape = false;
					b.append(c);
					continue;
				}
				if (c == '\\') {
					escape = true;
					continue;
				}
				if (c == ':') {
					strings.add(b.toString());
					b.setLength(0);
					continue;
				}
				b.append(c);
			}
			if (b.length() != 0) {
				strings.add(b.toString());
			}
			return strings.toArray(new String[strings.size()]);
		};
		for (Entry<String, String> entry:prop.getEntries()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key == null || value == null) {
				continue;
			}
			if (key.startsWith("HOME->")) {
				try {
					String[] home = split.apply(key.substring("HOME->".length()));
					String[] positions = split.apply(value);
					
					String name = home[0];
					String owner = home[1];
					String world = positions[0];
					float x = Float.parseFloat(positions[1]);
					float y = Float.parseFloat(positions[2]);
					float z = Float.parseFloat(positions[3]);
					float pitch = Float.parseFloat(positions[4]);
					float yaw = Float.parseFloat(positions[5]);
					Home h = new Home(name, owner, world, x, y, z, pitch, yaw);
					addHome(h);
				} catch (Exception ex) {
					System.out.println("Erro ao carregar home '"+key+"': "+ex.getMessage());
				}
			} else if (key.startsWith("WARP->")) {
				try {
					String[] positions = split.apply(value);
					
					String name = key.substring("WARP->".length());
					String world = positions[0];
					float x = Float.parseFloat(positions[1]);
					float y = Float.parseFloat(positions[2]);
					float z = Float.parseFloat(positions[3]);
					float pitch = Float.parseFloat(positions[4]);
					float yaw = Float.parseFloat(positions[5]);
					Warp h = new Warp(name, world, x, y, z, pitch, yaw);
					addWarp(h);
				} catch (Exception ex) {
					System.out.println("Erro ao carregar warp '"+key+"': "+ex.getMessage());
				}
			}
		}
	}
	
	public Home[] getHomes(){
		return homes.toArray(new Home[homes.size()]);
	}
	
	public Warp[] getWarps() {
		return warps.toArray(new Warp[warps.size()]);
	}
	
	public Home[] getHomes(String owner) {
		List<Home> homes = new ArrayList<>();
		for (Home h:getHomes()) {
			if (h.getOwner().equals(owner)) {
				homes.add(h);
			}
		}
		return homes.toArray(new Home[homes.size()]);
	}
	
	public Home getHome(String name, String owner) {
		for (Home h:getHomes()) {
			if (h.getName().equalsIgnoreCase(name) && h.getOwner().equals(owner)) {
				return h;
			}
		}
		return null;
	}
	
	public Warp getWarp(String name) {
		for (Warp h:getWarps()) {
			if (h.getName().equalsIgnoreCase(name)) {
				return h;
			}
		}
		return null;
	}
	
	public boolean containsHome(String name, String owner) {
		for (Home h:getHomes()) {
			if (h.getName().equalsIgnoreCase(name) && h.getOwner().equals(owner)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsWarp(String name) {
		for (Warp h:getWarps()) {
			if (h.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean addHome(Home h) {
		if (homes.contains(h)) {
			return false;
		}
		String name = "HOME->"+escape(h.getName())+":"+escape(h.getOwner());
		String position = 
				h.getWorld() + ":" +
				h.getX() + ":" +
				h.getY() + ":" +
				h.getZ() + ":" +
				h.getPitch() + ":" +
				h.getYaw()
				;
		prop.set(name, position);
		homes.add(h);
		return true;
	}
	
	public boolean removeHome(Home h) {
		if (!homes.contains(h)) {
			return false;
		}
		String name = "HOME->"+escape(h.getName())+":"+escape(h.getOwner());
		prop.remove(name);
		return homes.remove(h);
	}
	
	public boolean addWarp(Warp w) {
		if (warps.contains(w)) {
			return false;
		}
		String name = "WARP->"+escape(w.getName());
		String position = 
				w.getWorld() + ":" +
				w.getX() + ":" +
				w.getY() + ":" +
				w.getZ() + ":" +
				w.getPitch() + ":" +
				w.getYaw()
				;
		prop.set(name, position);
		warps.add(w);
		return true;
	}
	
	public boolean removeWarp(Warp w) {
		if (!warps.contains(w)) {
			return false;
		}
		String name = "WARP->"+escape(w.getName());
		prop.remove(name);
		return warps.remove(w);
	}
	
	public int getMaxHomes(String player) {
		Properties prop = Properties.getProperties(player);
		String homes = prop.get("maxHomes");
		if (homes == null) {
			return DEFAULT_MAX_HOMES;
		}
		try {
			return Integer.parseInt(homes);
		} catch (NumberFormatException ex) {
			return DEFAULT_MAX_HOMES;
		}
	}
	
	public void setMaxHomes(String player, int homes) {
		Properties prop = Properties.getProperties(player);
		prop.set("maxHomes", Integer.toString(homes));
	}
	
	public int getNumberOfHomes(String player) {
		return getHomes(player).length;
	}
}
