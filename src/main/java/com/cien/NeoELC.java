package com.cien;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import com.cien.banitem.CienBanItem;
import com.cien.chat.CienChat;
import com.cien.claims.CienClaims;
import com.cien.commands.ClearEntities;
import com.cien.commands.Memory;
import com.cien.commands.Modules;
import com.cien.commands.Ping;
import com.cien.commands.TPS;
import com.cien.data.Properties;
import com.cien.discord.CienDiscord;
import com.cien.discord.commands.Discord;
import com.cien.discord.commands.Token;
import com.cien.economy.CienEconomy;
import com.cien.fakeplayers.CienFakePlayers;
import com.cien.kits.CienKits;
import com.cien.levels.CienLevels;
import com.cien.login.CienLogin;
import com.cien.permissions.CienPermissions;
import com.cien.superchat.SuperChatClickCommand;
import com.cien.teleport.CienTeleport;
import com.cien.utils.CienUtils;
import com.cien.vip.CienVIP;
import com.cien.votifier.CienVotifier;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

@Mod(modid = "NeoELC", version = "1.0", acceptedMinecraftVersions = "*", acceptableRemoteVersions = "*", acceptableSaveVersions = "*", name = "NeoELC")
public class NeoELC {
    
	long lastTime = 0;
	int ticks = 0;
	boolean ok = false;
	boolean utilStarted = false;
	long shutdown = System.currentTimeMillis() + (2*60*60*1000);
	long lastSec = 0;
	
	@EventHandler
	public void construct(FMLConstructionEvent event) {
		LaunchClassLoader classLoader = (LaunchClassLoader) getClass().getClassLoader();
    	classLoader.addClassLoaderExclusion("com.fasterxml");
    	classLoader.addClassLoaderExclusion("com.iwebpp");
    	classLoader.addClassLoaderExclusion("com.neovisionaries");
    	classLoader.addClassLoaderExclusion("net.dv8tion");
    	classLoader.addClassLoaderExclusion("okhttp3");
    	classLoader.addClassLoaderExclusion("okio");
    	classLoader.addClassLoaderExclusion("org.apache.commons.collections4");
    	classLoader.addClassLoaderExclusion("org.intellij.lang.annotations");
    	classLoader.addClassLoaderExclusion("org.jetbrains.annotations");
    	classLoader.addClassLoaderExclusion("org.slf4j");
    	
    	URLClassLoader parent = (URLClassLoader) classLoader.getClass().getClassLoader();
    	
    	System.out.println("Procurando pela biblioteca do Discord...");

    	File mods = new File("discord_jda");
    	if (!mods.exists()) {
    		mods.mkdirs();
    	}
    	File[] files = mods.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isFile()) {
					if (pathname.getName().endsWith(".jar")) {
						return true;
					}
				}
				return false;
			}
		});
		boolean suc = false;
    	try {
    		Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        	method.setAccessible(true);
    		for (File f:files) {
    			if (f.getName().startsWith("JDA")) {
    				method.invoke(parent, f.toURI().toURL());
    				suc = true;
    				break;
    			}
    		}
    	} catch (Exception ex) {
    		System.out.println("Erro enquanto carregava a biblioteca do discord");
    		ex.printStackTrace();
    	}
    	if (suc) {
    		System.out.println("Biblioteca do discord carregada.");
    	} else {
    		System.out.println("Não foi possível encontrar a biblioteca do discord");
    		System.out.println("Tenha certeza de que baixou a versão com as dependencias");
    		System.out.println("e que ela está na pasta discord_jda, com o nome começando com JDA.");
    		System.out.println("O Servidor irá desligar...");
    		FMLCommonHandler.instance().raiseException(new RuntimeException("JDA não encontrado."), "Veja o log.", true);
    	}
	}
	
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	System.out.println("Servidor Iniciando.. Alterando o Gerenciador de Comandos através de reflection.");
    	CienCommandManager manager = new CienCommandManager();
    	MinecraftServer server = MinecraftServer.getServer();
    	boolean sucesso = false;
    	try {
    		Field[] fields = MinecraftServer.class.getDeclaredFields();
    		for (Field f:fields) {
    			f.setAccessible(true);
    			Object obj = f.get(server);
    			if (obj == null) {
    				continue;
    			}
    			if (obj.getClass() == ServerCommandManager.class) {
    				f.set(server, manager);
    				sucesso = true;
    				break;
    			}
    		}
    	} catch (ReflectiveOperationException ex) {
    		System.out.println("Erro ao alterar o gerenciador de comandos");
    		ex.printStackTrace();
    	}
    	if (sucesso) {
    		System.out.println("Gerenciador de Comandos Alterado com Sucesso!");
    	} else {
    		System.out.println("Não foi possível alterar o Gerenciador de Comandos");
    	}
    	
    	try {
    		CienDiscord.DISCORD.startDiscord();
    	} catch (Exception ex) {
    		System.out.println("Não foi possível iniciar o bot do discord: "+ex.getMessage());
    	}
    	
    	CienDiscord.DISCORD.sendMessage(":eight_spoked_asterisk: Servidor iniciando... (FASE 1)");
    }
    
    
    @EventHandler
    public void post(FMLPostInitializationEvent event) {
    	
    	//Main
    	FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    	
        //CienPermissions
        ModuleManager.register(CienPermissions.PERMISSIONS);
        
        //CienClaims
        ModuleManager.register(CienClaims.CLAIMS);
        
    	//CienLogin
        ModuleManager.register(CienLogin.LOGIN);
        
        //CienChat
        ModuleManager.register(CienChat.CHAT);
        
        //CienKits
        ModuleManager.register(CienKits.KITS);
        
        //CienEconomy
        ModuleManager.register(CienEconomy.ECONOMY);
        
        //CienDiscord
        FMLCommonHandler.instance().bus().register(CienDiscord.DISCORD);
        MinecraftForge.EVENT_BUS.register(CienDiscord.DISCORD);
        
        //CienVIP
        ModuleManager.register(CienVIP.VIP);
        
        //CienBanItem
        ModuleManager.register(CienBanItem.BANITEM);
        
        //CienVotifier
        ModuleManager.register(CienVotifier.VOTIFIER);
        
        //CienLevels
        ModuleManager.register(CienLevels.LEVELS);
        
        //CienUtils
        ModuleManager.register(CienUtils.UTILS);
        
        //CienFakePlayers
        ModuleManager.register(CienFakePlayers.FAKEPLAYERS);
        
        //CienTeleport
        ModuleManager.register(CienTeleport.TELEPORT);
        
        CienDiscord.DISCORD.sendMessage(":eight_spoked_asterisk: Servidor iniciando... (FASE 2)");
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    	//Main Commands
    	event.registerServerCommand(new Memory());
    	event.registerServerCommand(new TPS());
    	event.registerServerCommand(new Ping());
    	event.registerServerCommand(new ClearEntities());
    	event.registerServerCommand(new Modules());
    	
    	//CienDiscord
    	event.registerServerCommand(new Discord());
    	event.registerServerCommand(new Token());
    	
    	//SuperChat
    	event.registerServerCommand(new SuperChatClickCommand()); //internal use
    	
    	ModuleManager.registerCommands(event);
    	
    	CienDiscord.DISCORD.sendMessage(":eight_spoked_asterisk: Servidor iniciando... (FASE 3)");
    }
    
    @EventHandler
    public void serverShutdown(FMLServerStoppingEvent event) {
    	CienDiscord.DISCORD.setChannelsTopic("Servidor Offline.");
    	CienDiscord.DISCORD.sendMessage(":red_square: Servidor Desligando.");
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
    
    @EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
    	Properties.saveAll();
    	CienDiscord.DISCORD.sendMessage(":red_square: Servidor Desligado.");
    	Properties.cleanup();
    	CienVotifier.VOTIFIER.shutdown();
    }
    
    @SubscribeEvent
    public void serverTick(ServerTickEvent event) {
    	if (!utilStarted) {
    		utilStarted = true;
    		Util.load();
    		start();
    		ModuleManager.start();
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
    	
    	ModuleManager.tick();
    }
    
    public void start() {
    	CienUtils.UTILS.run(() -> {
    		System.out.println("Salvando dados...");
    		Properties.forEach(Properties::save);
    		System.out.println("Salvo.");
    	}, 6000, true);
    	CienUtils.UTILS.run(() -> {
    		long secs = (shutdown - System.currentTimeMillis())/1000;
    		if (secs > 30) {
    			return;
    		}
    		if (lastSec != secs && secs > 0) {
    			Util.sendMessageToEveryone(Util.getPrefix()+"Servidor reiniciando em "+secs+" segundo(s).");
    			CienDiscord.DISCORD.sendMessage(":regional_indicator_r: Reiniciando em "+secs+" segundos(s).");
    		} 
    		if (secs <= 0) {
    			Util.sendMessageToEveryone(Util.getPrefix()+"Servidor Reiniciando...");
    			CienDiscord.DISCORD.sendMessage(":regional_indicator_r: Servidor Reiniciando...");
    			MinecraftServer.getServer().initiateShutdown();
    		}
    		lastSec = secs;
    	}, 10, true);
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
    
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
    public void onDragonKilled(LivingDeathEvent event) {
    	if (event.entityLiving instanceof EntityDragon) {
    		Entity e = event.source.getSourceOfDamage();
    		if (e instanceof EntityPlayerMP) {
    			EntityPlayerMP player = (EntityPlayerMP) e;
    			Util.sendMessageToEveryone("§5O Dragão foi morto por "+player.getCommandSenderName()+"! ~gg:");
    		} else {
    			Util.sendMessageToEveryone("§5O Dragão foi morto! ~gg:");
    		}
    	}
    }
    
}
