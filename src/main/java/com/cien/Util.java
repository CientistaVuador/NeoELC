package com.cien;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;

public class Util {
	
	protected static List<Task> tasks = new ArrayList<>();
	protected static List<ScheduledTask> scheduled = new ArrayList<>();
	protected static int TPS = 0;
	
	public static Task run(String name, Runnable r, int ticks) {
		Task t = new Task(name, r, ticks);
		tasks.add(t);
		return t;
	}
	
	public static Task run(String name, Runnable r) {
		Task t = new Task(name, r, 0);
		tasks.add(t);
		return t;
	}
	
	public static ScheduledTask schedule(String name, Runnable r, int ticks) {
		ScheduledTask t = new ScheduledTask(name, r, ticks);
		scheduled.add(t);
		return t;
	}
	
	public static String getModExclusivePrefix() {
		return "(EXC)";
	}
	
	public static int getTPS() {
		return TPS;
	}
	
	public static ServerConfigurationManager getServerManager() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
	}
	
	public static void sendMessageToEveryone(String msg) {
		ServerConfigurationManager manager = getServerManager();
		manager.sendChatMsg(new ChatComponentText(fixColors(msg)));
	}
	
	public static String fixColors(String msg) {
		StringBuilder b = new StringBuilder(64);
		char lastColor = 'f';
		boolean color = false;
		for (char c:msg.toCharArray()) {
			if (color) {
				color = false;
				lastColor = c;
			}
			if (c == '§') {
				color = true;
			}
			if (c == ' ') {
				b.append(' ');
				b.append('§');
				b.append(lastColor);
				continue;
			}
			b.append(c);
		}
		return b.toString();
	}
	
	public static String getPrefix() {
		return "§8§l[§bNeo§6ELC§8§l]§6 ";
	}
	
	private Util() {
		
	}
}
