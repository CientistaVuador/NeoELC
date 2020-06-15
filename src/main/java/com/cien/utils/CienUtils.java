package com.cien.utils;

import java.util.HashMap;
import java.util.Map;
import com.cien.Module;
import com.cien.Util;
import com.cien.claims.Claim;
import com.cien.data.Properties;
import com.cien.utils.commands.Claimlag;
import com.cien.utils.commands.Invsee;
import com.cien.utils.commands.Lagtop;
import com.cien.utils.commands.Lanterna;
import com.cien.utils.commands.Tempo;
import com.cien.utils.commands.Tiletick;
import com.cien.utils.commands.Vanish;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class CienUtils extends Module {

	
	public static final CienUtils UTILS = new CienUtils();
	
	private final Map<Class<?>, Long> timeCache = new HashMap<>();
	private final Map<Claim, Long> claimTimeCache = new HashMap<>();
	private long nextClaimCacheReset = 0;
	private long nextPlayerTimeUpdate = 0;
	
	private CienUtils() {
		super("CienUtils");
	}
	
	@Override
	public void start() {
		nextPlayerTimeUpdate = System.currentTimeMillis() + 1000;
	}
	
	@Override
	public void tick() {
		if (System.currentTimeMillis() >= nextPlayerTimeUpdate) {
			nextPlayerTimeUpdate = System.currentTimeMillis() + 1000;
			for (EntityPlayerMP player:Util.getOnlinePlayers()) {
				Properties prop = Properties.getProperties(player.getCommandSenderName());
				String currentTimeStr = prop.get("currentTimeOnline");
				long currentTime = 0;
				if (currentTimeStr != null) {
					try {
						currentTime = Long.parseLong(currentTimeStr);
					} catch (NumberFormatException ex) {
						ex.printStackTrace();
					}
				}
				currentTime += 1000;
				prop.set("currentTimeOnline", Long.toString(currentTime));
			}
		}
	}
	
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new Claimlag());
		event.registerServerCommand(new Invsee());
		event.registerServerCommand(new Lagtop());
		event.registerServerCommand(new Tiletick());
		event.registerServerCommand(new Vanish());
		event.registerServerCommand(new Tempo());
		event.registerServerCommand(new Lanterna());
	}
	
	public long getOnlineTimeOf(String player) {
		Properties prop = Properties.getProperties(player);
		String time = prop.get("currentTimeOnline");
		if (time != null) {
			try {
				return Long.parseLong(time);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				return 0;
			}
		}
		return 0;
	}
	
	public long getTileEntityTickTime(TileEntity ent) {
		if (ent == null) {
			return 0;
		}
		Long cache = timeCache.get(ent.getClass());
		if (cache != null) {
			return cache;
		}
		try {
			long medium = 0;
			for (int i = 0; i < 3; i++) {
				long here = System.nanoTime();
				ent.updateEntity();
				medium += (System.nanoTime() - here);
			}
			long time = medium/3;
			timeCache.put(ent.getClass(), time);
			return time;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	public long getMediumTickTimeOf(Claim c) {
		if (System.currentTimeMillis() >= nextClaimCacheReset) {
			nextClaimCacheReset = System.currentTimeMillis() + 10*60*1000;
			claimTimeCache.clear();
		} else {
			Long cache = claimTimeCache.get(c);
			if (cache != null) {
				return cache;
			}
		}
		WorldServer world = Util.getWorld(c.getWorld());
		if (world == null) {
			return 0;
		}
		long tiles = 0;
		long time = 0;
		for (int y = 0; y < 256; y++) {
			for (int x = c.getSmallerX(); x < (c.getBiggerX()+1); x++) {
				for (int z = c.getSmallerZ(); z < (c.getBiggerZ()+1); z++) {
					TileEntity ent = world.getTileEntity(x, y, z);
					if (ent != null) {
						time += getTileEntityTickTime(ent);
						tiles++;
					}
				}
			}
		}
		if (tiles == 0 || time == 0) {
			return 0;
		}
		return time/tiles;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
			EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
			if (player != null) {
				Properties prop = Properties.getProperties(player.getCommandSenderName());
				Object obj = prop.getMemory("TILETICK_");
				if (obj != null) {
					TileEntity ent = player.worldObj.getTileEntity(event.x, event.y, event.z);
					if (ent != null) {
						int ticks = (int) obj;
						long nanosNow = System.nanoTime();
						prop.setMemory("TILETICK_", null);
						try {
							for (int i = 0; i < ticks; i++) {
								ent.updateEntity();
							}
						} catch (Throwable th) {
							Util.sendMessage(player, Util.getPrefix()+"Erro ao forçar tick: "+th.getMessage());
							return;
						}
						Util.sendMessage(player, Util.getPrefix()+"Tempo de "+ticks+" tick(s): "+(System.nanoTime() - nanosNow)+" nanos");
					}
				}
			}
		}
	}
	
}
