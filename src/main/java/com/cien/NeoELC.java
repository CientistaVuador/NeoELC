package com.cien;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import com.cien.banitem.CienBanItem;
import com.cien.banitem.commands.BanItem;
import com.cien.chat.CienChat;
import com.cien.chat.commands.Desmutar;
import com.cien.chat.commands.Global;
import com.cien.chat.commands.Mutar;
import com.cien.chat.commands.Privado;
import com.cien.chat.commands.Real;
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
import com.cien.claims.commands.IgnoreClaims;
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
import com.cien.commands.ClearEntities;
import com.cien.commands.Memory;
import com.cien.commands.Ping;
import com.cien.commands.TPS;
import com.cien.data.Properties;
import com.cien.discord.CienDiscord;
import com.cien.discord.commands.Discord;
import com.cien.discord.commands.Token;
import com.cien.economy.CienEconomy;
import com.cien.economy.commands.Cloja;
import com.cien.economy.commands.Comprar;
import com.cien.economy.commands.Eco;
import com.cien.economy.commands.Enviar;
import com.cien.economy.commands.Loja;
import com.cien.economy.commands.Money;
import com.cien.economy.commands.SetShop;
import com.cien.economy.commands.Top;
import com.cien.economy.commands.Vender;
import com.cien.kits.CienKits;
import com.cien.kits.commands.KitBuilder;
import com.cien.login.CienLogin;
import com.cien.login.commands.Login;
import com.cien.login.commands.Register;
import com.cien.login.commands.SetPassword;
import com.cien.permissions.CienPermissions;
import com.cien.permissions.commands.Perms;
import com.cien.teleport.commands.DelHome;
import com.cien.teleport.commands.DelWarp;
import com.cien.teleport.commands.GotoHome;
import com.cien.teleport.commands.Home;
import com.cien.teleport.commands.Rtp;
import com.cien.teleport.commands.SetHome;
import com.cien.teleport.commands.SetMaxHomes;
import com.cien.teleport.commands.SetWarp;
import com.cien.teleport.commands.Tpa;
import com.cien.teleport.commands.Tpac;
import com.cien.teleport.commands.Tphere;
import com.cien.teleport.commands.Tpp;
import com.cien.teleport.commands.Tprc;
import com.cien.teleport.commands.Warp;
import com.cien.vip.CienVIP;
import com.cien.vip.commands.Ativar;
import com.cien.vip.commands.GerarKey;
import com.cien.votifier.CienVotifier;
import com.cien.votifier.commands.Caixa;
import com.cien.votifier.commands.Vote;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

@Mod(modid = "NeoELC", version = "1.0", acceptedMinecraftVersions = "*", acceptableRemoteVersions = "*", acceptableSaveVersions = "*", name = "NeoELC")
public class NeoELC {
    
	long lastTime = 0;
	int ticks = 0;
	boolean ok = false;
	boolean utilStarted = false;
	long shutdown = System.currentTimeMillis() + (2*60*60*1000);
	long lastSec = 0;
	
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	System.out.println("NeoELC Iniciado!");
    	Util.schedule("Salvar Dados", () -> {
    		System.out.println("Salvando dados...");
    		Properties.forEach(Properties::save);
    	}, 6000);
    	Util.schedule("Shutdown", () -> {
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
    	}, 10);
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
    	
    	try {
    		CienDiscord.DISCORD.start();
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
        FMLCommonHandler.instance().bus().register(CienPermissions.PERMISSIONS);
        MinecraftForge.EVENT_BUS.register(CienPermissions.PERMISSIONS);
        
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
        
        //CienEconomy
        FMLCommonHandler.instance().bus().register(CienEconomy.ECONOMY);
        MinecraftForge.EVENT_BUS.register(CienEconomy.ECONOMY);
        
        //CienDiscord
        FMLCommonHandler.instance().bus().register(CienDiscord.DISCORD);
        MinecraftForge.EVENT_BUS.register(CienDiscord.DISCORD);
        
        //CienVIP
        FMLCommonHandler.instance().bus().register(CienVIP.VIP);
        MinecraftForge.EVENT_BUS.register(CienVIP.VIP);
        
        //CienBanItem
        FMLCommonHandler.instance().bus().register(CienBanItem.BANITEM);
        MinecraftForge.EVENT_BUS.register(CienBanItem.BANITEM);
        
        //CienVotifier
        FMLCommonHandler.instance().bus().register(CienVotifier.VOTIFIER);
        MinecraftForge.EVENT_BUS.register(CienVotifier.VOTIFIER);
        
        CienDiscord.DISCORD.sendMessage(":eight_spoked_asterisk: Servidor iniciando... (FASE 2)");
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    	//Main commands
    	event.registerServerCommand(new Memory());
    	event.registerServerCommand(new TPS());
    	event.registerServerCommand(new Ping());
    	event.registerServerCommand(new ClearEntities());
    	
    	//CienLogin
    	event.registerServerCommand(new Login());
    	event.registerServerCommand(new Register());
    	event.registerServerCommand(new SetPassword());
    	
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
    	event.registerServerCommand(new Rtp());
    	
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
    	event.registerServerCommand(new Real());
    	
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
    	event.registerServerCommand(new IgnoreClaims());
    	
    	//CienKits
    	event.registerServerCommand(new KitBuilder());
    	event.registerServerCommand(new com.cien.kits.commands.Kit());
    	
    	//CienEconomy
    	event.registerServerCommand(new Money());
    	event.registerServerCommand(new Enviar());
    	event.registerServerCommand(new Eco());
    	event.registerServerCommand(new Top());
    	event.registerServerCommand(new Cloja());
    	event.registerServerCommand(new SetShop());
    	event.registerServerCommand(new Loja());
    	event.registerServerCommand(new Comprar());
    	event.registerServerCommand(new Vender());
    	
    	//CienDiscord
    	event.registerServerCommand(new Discord());
    	event.registerServerCommand(new Token());
    	
    	//CienVIP
    	event.registerServerCommand(new GerarKey());
    	event.registerServerCommand(new Ativar());
    	event.registerServerCommand(new com.cien.vip.commands.Vip());
    	
    	//CienBanItem
    	event.registerServerCommand(new BanItem());
    	
    	//CienVotifier
    	event.registerServerCommand(new Caixa());
    	event.registerServerCommand(new Vote());
    	
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
