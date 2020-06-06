package com.cien.announcer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.cien.Module;
import com.cien.Util;
import com.cien.announcer.commands.Announcer;
import com.cien.data.Properties;
import com.cien.discord.CienDiscord;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CienAnnouncer extends Module {

	public static final CienAnnouncer ANNOUNCER = new CienAnnouncer();
	
	private final Properties prop = Properties.getProperties("(Module)CienAnnouncer");
	private final List<String> ann = new ArrayList<>();
	private long next = 0;
	
	public CienAnnouncer() {
		super("CienAnnouncer");
	}
	
	@Override
	public void start() {
		next = System.currentTimeMillis() + (getAnnouncerTime() * 1000);
		ann.addAll(Arrays.asList(prop.getArray("announces")));
	}
	
	@Override
	public void tick() {
		if (System.currentTimeMillis() >= next) {
			next = System.currentTimeMillis() + (getAnnouncerTime() * 1000);
			String[] a = getAnnouncements();
			if (a.length == 0) {
				return;
			}
			String target = a[(int) (Math.random() * a.length)];
			announce(target);
		}
	}
	
	public void announce(String s) {
		String[] parsed = parse(s);
		for (String f:parsed) {
			Util.sendMessageToEveryone(f);
			CienDiscord.DISCORD.sendMessage(":newspaper: "+Util.discordColorsToBlackAndWhite(f));
		}
	}
	
	public boolean addAnnouncement(String s) {
		if (ann.contains(s)) {
			return false;
		}
		ann.add(s);
		prop.setArray("announces", ann.toArray(new String[ann.size()]));
		return true;
	}
	
	public boolean removeAnnouncement(String s) {
		boolean b = ann.remove(s);
		if (b) {
			prop.setArray("announces", ann.toArray(new String[ann.size()]));
		}
		return true;
	}
	
	public String[] getAnnouncements() {
		return ann.toArray(new String[ann.size()]);
	}
	
	public String getAnnouncement(int index) {
		return ann.get(index);
	}
	
	public int getSize() {
		return ann.size();
	}
	
	public void removeAnnouncement(int index) {
		ann.remove(index);
		prop.setArray("announces", ann.toArray(new String[ann.size()]));
	}
	
	public String[] parse(String a) {
		List<String> list = new ArrayList<>();
		StringBuilder builder = new StringBuilder(a.length());
		boolean escape = false;
		for (char c:a.toCharArray()) {
			if (c == '&') {
				if (escape) {
					escape = false;
					builder.append(c);
				} else {
					builder.append('§');
				}
				continue;
			}
			if (c == 'p' && escape) {
				escape = false;
				builder.append(Util.getPrefix());
				continue;
			}
			if (c == 't' && escape) {
				escape = false;
				builder.append("    ");
				continue;
			}
			if (c == 'n' && escape) {
				escape = false;
				list.add(builder.toString());
				builder.setLength(0);
				continue;
			}
			if (escape) {
				escape = false;
				builder.append('\\');
				builder.append(c);
				continue;
			}
			if (c == '\\') {
				escape = true;
				continue;
			}
			builder.append(c);
		}
		if (builder.length() > 0) {
			list.add(builder.toString());
		}
		return list.toArray(new String[list.size()]);
	}
	
	public int getAnnouncerTime() {
		String time = prop.get("time");
		if (time == null) {
			return 5*60;
		}
		return Integer.parseInt(time);
	}
	
	public void setAnnouncerTime(int time) {
		prop.set("time", Integer.toString(time));
		next = System.currentTimeMillis() + (getAnnouncerTime() * 1000);
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new Announcer());
	}
	
}
