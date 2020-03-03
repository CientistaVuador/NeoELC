package com.cien;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

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
	
	public static void teleportPlayer(EntityPlayerMP player, World w, float x, float y, float z, float pitch, float yaw) {
		int oldDim = player.worldObj.provider.dimensionId;
		if (oldDim != w.provider.dimensionId) {
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, w.provider.dimensionId);
		}
		player.setPositionAndRotation(x, y, z, yaw, pitch);
		player.setPositionAndUpdate(x, y, z);
		player.addExperienceLevel(0);
		if (oldDim == 1 && oldDim != w.provider.dimensionId) {
			Util.run("Remove player from end: "+player.getDisplayName(), () -> {
				player.mcServer.getConfigurationManager().transferPlayerToDimension(player, w.provider.dimensionId);
	            player.setPositionAndUpdate(x, y, z);
	            player.getServerForPlayer().updateEntityWithOptionalForce(player, false);
			}, 2);
        }
	}
	
	public static int getTPS() {
		return TPS;
	}
	
	public static WorldServer getWorld(String name) {
		for (WorldServer sv:DimensionManager.getWorlds()) {
			if (sv.provider.getDimensionName().equals(name)) {
				return sv;
			}
		}
		return null;
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
