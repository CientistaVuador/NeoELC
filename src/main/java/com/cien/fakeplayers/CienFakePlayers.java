package com.cien.fakeplayers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cien.Module;
import com.cien.data.Properties;
import com.cien.fakeplayers.commands.FakePlayers;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

public class CienFakePlayers extends Module {
	
	public static final CienFakePlayers FAKEPLAYERS = new CienFakePlayers();
	
	private Properties prop = Properties.getProperties("(Module)CienFakePlayers");
	
	private CienFakePlayers() {
		super("CienFakePlayers");
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new FakePlayers());
	}
	
	public boolean isFakePlayer(String name) {
		for (char c:name.toCharArray()) {
			if (c != '_') {
				if (c < 'a' || c > 'z') {
					if (c < 'A' || c > 'Z') {
						if (c < '0' || c > '9') {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean addFakePlayer(String player) {
		List<String> players = new ArrayList<>(Arrays.asList(prop.getArray("fakeplayers")));
		if (players.contains(player)) {
			return false;
		}
		players.add(player);
		prop.setArray("fakeplayers", players.toArray(new String[players.size()]));
		return true;
	}
	
	public boolean removeFakePlayer(String player) {
		List<String> players = new ArrayList<>(Arrays.asList(prop.getArray("fakeplayers")));
		if (players.contains(player)) {
			players.remove(player);
			prop.setArray("fakeplayers", players.toArray(new String[players.size()]));
			return true;
		}
		return false;
	}
	
	public String[] getFakePlayers() {
		return prop.getArray("fakeplayers");
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onBlockBreak(BreakEvent event) {
		if (event.getPlayer() == null) {
			return;
		}
		if (isFakePlayer(event.getPlayer().getCommandSenderName())) {
			addFakePlayer(event.getPlayer().getCommandSenderName());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onBlockPlace(PlaceEvent event) {
		if (event.player == null) {
			return;
		}
		if (isFakePlayer(event.player.getCommandSenderName())) {
			addFakePlayer(event.player.getCommandSenderName());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.entityPlayer == null) {
			return;
		}
		if (isFakePlayer(event.entityPlayer.getCommandSenderName())) {
			addFakePlayer(event.entityPlayer.getCommandSenderName());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onEntityHurt(LivingHurtEvent at) {
		if (at.source == null) {
			return;
		}
		if (at.source.getSourceOfDamage() == null) {
			return;
		}
		if (at.source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) at.source.getSourceOfDamage();
			if (isFakePlayer(player.getCommandSenderName())) {
				addFakePlayer(player.getCommandSenderName());
			}
		}
	}
}
