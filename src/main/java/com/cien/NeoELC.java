package com.cien;

import com.cien.chat.CienChat;
import com.cien.chat.commands.Desmutar;
import com.cien.chat.commands.Global;
import com.cien.chat.commands.Mutar;
import com.cien.chat.commands.Privado;
import com.cien.chat.commands.Responder;
import com.cien.chat.commands.SetNick;
import com.cien.chat.commands.SetPrefix;
import com.cien.chat.commands.Staff;
import com.cien.chat.commands.Vip;
import com.cien.claims.CienClaims;
import com.cien.claims.commands.Abandonclaim;
import com.cien.claims.commands.BlockedItems;
import com.cien.claims.commands.Blocks;
import com.cien.claims.commands.Claim;
import com.cien.claims.commands.ClaimAtual;
import com.cien.claims.commands.DangerousEntities;
import com.cien.claims.commands.ETrust;
import com.cien.claims.commands.EntidadesClaim;
import com.cien.claims.commands.Expand;
import com.cien.claims.commands.MeusClaims;
import com.cien.claims.commands.Pos1;
import com.cien.claims.commands.Pos2;
import com.cien.claims.commands.SetBlockedItem;
import com.cien.claims.commands.SetDangerousEntity;
import com.cien.claims.commands.SetFlag;
import com.cien.claims.commands.TpClaim;
import com.cien.claims.commands.TransferirClaim;
import com.cien.claims.commands.Trust;
import com.cien.claims.commands.TrustList;
import com.cien.claims.commands.Untrust;
import com.cien.claims.commands.VerFlags;
import com.cien.commands.Memory;
import com.cien.commands.TPS;
import com.cien.data.Properties;
import com.cien.kits.CienKits;
import com.cien.kits.commands.KitBuilder;
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
import com.cien.teleport.commands.Tpa;
import com.cien.teleport.commands.Tpac;
import com.cien.teleport.commands.Tphere;
import com.cien.teleport.commands.Tpp;
import com.cien.teleport.commands.Tprc;
import com.cien.teleport.commands.Warp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

@Mod(modid = "NeoELC", version = "1.0", acceptedMinecraftVersions = "*", acceptableRemoteVersions = "*", acceptableSaveVersions = "*", name = "NeoELC")
public class NeoELC {
    
	long lastTime = 0;
	int ticks = 0;
	boolean ok = false;
	boolean utilStarted = false;
	
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
        
        //CienChat
        FMLCommonHandler.instance().bus().register(CienChat.CHAT);
        MinecraftForge.EVENT_BUS.register(CienChat.CHAT);
        
        //CienClaims
        FMLCommonHandler.instance().bus().register(CienClaims.CLAIMS);
        MinecraftForge.EVENT_BUS.register(CienClaims.CLAIMS);
        
        //CienKits
        FMLCommonHandler.instance().bus().register(CienKits.KITS);
        MinecraftForge.EVENT_BUS.register(CienKits.KITS);
        
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
    	event.registerServerCommand(new Tpa());
    	event.registerServerCommand(new Tprc());
    	event.registerServerCommand(new Tpac());
    	event.registerServerCommand(new Tphere());
    	event.registerServerCommand(new Tpp());
    	
    	//CienChat
    	event.registerServerCommand(new Global());
    	event.registerServerCommand(new SetNick());
    	event.registerServerCommand(new SetPrefix());
    	event.registerServerCommand(new Desmutar());
    	event.registerServerCommand(new Mutar());
    	event.registerServerCommand(new Vip());
    	event.registerServerCommand(new Staff());
    	event.registerServerCommand(new Privado());
    	event.registerServerCommand(new Responder());
    	
    	//CienClaims
    	event.registerServerCommand(new Blocks());
    	event.registerServerCommand(new Claim());
    	event.registerServerCommand(new ClaimAtual());
    	event.registerServerCommand(new Pos1());
    	event.registerServerCommand(new Pos2());
    	event.registerServerCommand(new Expand());
    	event.registerServerCommand(new SetFlag());
    	event.registerServerCommand(new VerFlags());
    	event.registerServerCommand(new SetBlockedItem());
    	event.registerServerCommand(new BlockedItems());
    	event.registerServerCommand(new EntidadesClaim());
    	event.registerServerCommand(new SetDangerousEntity());
    	event.registerServerCommand(new DangerousEntities());
    	event.registerServerCommand(new MeusClaims());
    	event.registerServerCommand(new TpClaim());
    	event.registerServerCommand(new Abandonclaim());
    	event.registerServerCommand(new TransferirClaim());
    	event.registerServerCommand(new TrustList());
    	event.registerServerCommand(new Trust());
    	event.registerServerCommand(new ETrust());
    	event.registerServerCommand(new Untrust());
    	
    	//CienKits
    	event.registerServerCommand(new KitBuilder());
    	event.registerServerCommand(new com.cien.kits.commands.Kit());
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
    	if (!utilStarted) {
    		utilStarted = true;
    		Util.load();
    	}
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
    
    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = false)
	public void onCommand(CommandEvent event) {
    	StringBuilder builder = new StringBuilder(64);
    	for (int i = 0; i < event.parameters.length; i++) {
    		builder.append(event.parameters[i]);
    		if (i != (event.parameters.length - 1)) {
    			builder.append(' ');
    		}
    	}
		System.out.println(event.sender.getCommandSenderName()+" executou /"+event.command.getCommandName()+" "+builder.toString());
	}
    
}
