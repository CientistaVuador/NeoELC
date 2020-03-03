package com.cien;

import com.cien.commands.Memory;
import com.cien.commands.TPS;
import com.cien.data.Properties;
import com.cien.login.CienLogin;
import com.cien.login.commands.Login;
import com.cien.login.commands.Register;
import com.cien.permissions.commands.Perms;
import com.cien.teleport.commands.DelHome;
import com.cien.teleport.commands.DelWarp;
import com.cien.teleport.commands.GotoHome;
import com.cien.teleport.commands.Home;
import com.cien.teleport.commands.SetHome;
import com.cien.teleport.commands.SetMaxHomes;
import com.cien.teleport.commands.SetWarp;
import com.cien.teleport.commands.Warp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "NeoELC", version = "1.0", acceptedMinecraftVersions = "*", acceptableRemoteVersions = "*", acceptableSaveVersions = "*", name = "NeoELC")
public class NeoELC {
    
	long lastTime = 0;
	int ticks = 0;
	boolean ok = false;
	
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	System.out.println("NeoELC Iniciado!");
    	Util.schedule("Salvar Dados", () -> {
    		System.out.println("Salvando dados...");
    		Properties.forEach(Properties::save);
    	}, 6000);
    }
    
    @EventHandler
    public void post(FMLPostInitializationEvent event) {
    	//Main
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    	
    	//CienLogin
        FMLCommonHandler.instance().bus().register(CienLogin.LOGIN);
        MinecraftForge.EVENT_BUS.register(CienLogin.LOGIN);
        
        
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    	//Main commands
    	event.registerServerCommand(new Memory());
    	event.registerServerCommand(new TPS());
    	
    	//CienLogin
    	event.registerServerCommand(new Login());
    	event.registerServerCommand(new Register());
    	
    	//CienPermissions
    	event.registerServerCommand(new Perms());
    	
    	//CienTeleport
    	event.registerServerCommand(new DelHome());
    	event.registerServerCommand(new DelWarp());
    	event.registerServerCommand(new GotoHome());
    	event.registerServerCommand(new Home());
    	event.registerServerCommand(new SetHome());
    	event.registerServerCommand(new SetMaxHomes());
    	event.registerServerCommand(new SetWarp());
    	event.registerServerCommand(new Warp());
    }
    
    @EventHandler
    public void serverShutdown(FMLServerStoppingEvent event) {
    	System.out.println("Salvando dados...");
		Properties.forEach(Properties::save);
		for (Object player:MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP ent = (EntityPlayerMP) player;
			if (CienLogin.LOGIN.shouldBeFreezed(ent.getCommandSenderName())) {
				if (Properties.hasProperties(ent.getCommandSenderName())) {
					Properties prop = Properties.getProperties(ent.getCommandSenderName());
					ItemStack[][] stack = (ItemStack[][]) prop.getMemory("storedInv");
		    		if (stack != null) {
		    			ItemStack[] armor = stack[0];
		    			ItemStack[] inv = stack[1];
		    			ent.inventory.armorInventory = armor;
		    			ent.inventory.mainInventory = inv;
		    		}
				}
			}
		}
    }
    
    @SubscribeEvent
    public void serverTick(ServerTickEvent event) {
    	//Cut the tick rate by half
    	if (ok) {
    		ok = false;
    		return;
    	}
    	ok = true;
    	
    	//TPS counter
    	ticks++;
    	if (System.currentTimeMillis() - lastTime >= 1000) {
    		Util.TPS = ticks;
    		ticks = 0;
    		lastTime = System.currentTimeMillis();
    	}
    	
    	//Tasks
    	for (Task t:Util.tasks.toArray(new Task[Util.tasks.size()])) {
    		t.tick();
    		if (t.isComplete()) {
    			Util.tasks.remove(t);
    		}
    	}
    	for (ScheduledTask t:Util.scheduled.toArray(new ScheduledTask[Util.scheduled.size()]) ) {
    		t.tick();
    		if (t.isComplete()) {
    			Util.scheduled.remove(t);
    		}
    	}
    }
}
