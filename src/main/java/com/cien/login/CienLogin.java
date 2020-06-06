package com.cien.login;

import java.util.ArrayList;
import java.util.List;
import com.cien.Module;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.login.commands.Login;
import com.cien.login.commands.Register;
import com.cien.login.commands.SetPassword;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class CienLogin extends Module {
	
	public static final CienLogin LOGIN = new CienLogin();
	
	public static final List<EntityPlayerMP> NEED_LOGIN = new ArrayList<>();
	public static final List<EntityPlayerMP> NEED_REGISTER = new ArrayList<>();
	
	private CienLogin() {
		super("CienLogin");
	}
	
	@Override
	public void start() {
		run(new ModuleRunnable() {
			@Override
			public void run(Module mdl, ModuleRunnable r) {
				for (EntityPlayerMP player:NEED_LOGIN.toArray(new EntityPlayerMP[NEED_LOGIN.size()])) {
					player.addChatMessage(Util.fixColors(Util.getPrefix()+"Faça login com /login <Senha>"));
				}
				for (EntityPlayerMP player:NEED_REGISTER.toArray(new EntityPlayerMP[NEED_REGISTER.size()])) {
					player.addChatMessage(Util.fixColors(Util.getPrefix()+"Faça seu registro com /register <Senha> <Senha>"));
				}
			}
		}, 100, true);
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new Login());
    	event.registerServerCommand(new Register());
    	event.registerServerCommand(new SetPassword());
	}
	
	public String getPassword(String player) {
		Properties prop = Properties.getProperties(player);
		return prop.get("password");
	}
	
	public void setPassword(String player, String password) {
		Properties prop = Properties.getProperties(player);
		prop.set("password", password);
	}
	
	public boolean isBuggy(String player) {
		EntityPlayerMP p = Util.getOnlinePlayer(player);
		if (p == null) {
			return false;
		}
		int targetId = Block.getIdFromBlock(Blocks.portal);
		for (int x = (int)(p.posX-5); x <= (int)(p.posX+5); x++) {
			for (int z = (int)(p.posZ-5); z <= (int)(p.posZ+5); z++) {
				for (int y = (int)(p.posY-5); y <= (int)(p.posY+5); y++) {
					if (y >= 0) {
						Block c = p.worldObj.getBlock(x, y, z);
						int id = Block.getIdFromBlock(c);
						if (id == targetId) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean shouldBeFreezed(String name) {
		for (EntityPlayerMP player:NEED_LOGIN.toArray(new EntityPlayerMP[NEED_LOGIN.size()])) {
			if (player.getCommandSenderName().equals(name)) {
				return true;
			}
		}
		for (EntityPlayerMP player:NEED_REGISTER.toArray(new EntityPlayerMP[NEED_REGISTER.size()])) {
			if (player.getCommandSenderName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerEntersServer(PlayerLoggedInEvent event) {
		System.out.println("UUID de "+event.player.getCommandSenderName()+" é "+event.player.getUniqueID().toString());
    	if (getPassword(event.player.getCommandSenderName()) == null) {
    		NEED_REGISTER.add((EntityPlayerMP)event.player);
    	} else {
    		EntityPlayerMP p = (EntityPlayerMP) event.player;
    		Properties prop = Properties.getProperties(p.getCommandSenderName());
    		String ip = prop.get("lastLoginIP");
    		if (ip != null && p.getPlayerIP().equals(ip)) {
    			Util.sendMessage(p, Util.getPrefix()+"Logado via ip, login não é necessário.");
    		} else {
    			NEED_LOGIN.add(p);
    		}
    	}
    }
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerExit(PlayerLoggedOutEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			NEED_LOGIN.remove(player);
			NEED_REGISTER.remove(player);
		}
	}
	
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerWalk(LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
			if (isBuggy(player.getCommandSenderName())) {
				return;
			}
			if (shouldBeFreezed(player.getCommandSenderName())) {
				player.setPositionAndUpdate(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(CommandEvent event) {
		String lowerCase = event.command.getCommandName().toLowerCase();
		if (shouldBeFreezed(event.sender.getCommandSenderName())) {
			if (!lowerCase.equals("register") && !lowerCase.equals("login")) {
				event.sender.addChatMessage(Util.fixColors(Util.getPrefix()+"§6Faça login/registro primeiro."));
				event.setCanceled(true);
			}
		}
	}
	
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (shouldBeFreezed(event.entityPlayer.getCommandSenderName())) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(ItemTossEvent event) {
		if (shouldBeFreezed(event.player.getCommandSenderName())) {
			ItemStack stack = event.entityItem.getEntityItem();
			event.setCanceled(true);
			event.player.inventory.addItemStackToInventory(stack);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(EntityItemPickupEvent event) {
		if (shouldBeFreezed(event.entityPlayer.getCommandSenderName())) {
			event.setCanceled(true);
		}
	}
}
