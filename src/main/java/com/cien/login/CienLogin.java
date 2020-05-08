package com.cien.login;

import java.util.ArrayList;
import java.util.List;

import com.cien.Util;
import com.cien.data.Properties;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class CienLogin {
	
	public static final CienLogin LOGIN = new CienLogin();
	
	public static final List<EntityPlayerMP> NEED_LOGIN = new ArrayList<>();
	public static final List<EntityPlayerMP> NEED_REGISTER = new ArrayList<>();
	
	public CienLogin() {
		System.out.println("CienLogin Iniciado!");
		Util.schedule("Aviso Register/Login", () -> {
			for (EntityPlayerMP player:NEED_LOGIN.toArray(new EntityPlayerMP[NEED_LOGIN.size()])) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Faça login com /login <Senha>")));
			}
			for (EntityPlayerMP player:NEED_REGISTER.toArray(new EntityPlayerMP[NEED_REGISTER.size()])) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Faça seu registro com /register <Senha> <Senha>")));
			}
		}, 100);
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
		if (Block.getIdFromBlock(p.worldObj.getBlock((int)p.posX, (int)p.posY, (int)p.posZ)) == Block.getIdFromBlock(Blocks.portal)) {
			return true;
		}
		if (Block.getIdFromBlock(p.worldObj.getBlock((int)p.posX+1, (int)p.posY, (int)p.posZ)) == Block.getIdFromBlock(Blocks.portal)) {
			return true;
		}
		if (Block.getIdFromBlock(p.worldObj.getBlock((int)p.posX-1, (int)p.posY, (int)p.posZ)) == Block.getIdFromBlock(Blocks.portal)) {
			return true;
		}
		if (Block.getIdFromBlock(p.worldObj.getBlock((int)p.posX, (int)p.posY, (int)p.posZ+1)) == Block.getIdFromBlock(Blocks.portal)) {
			return true;
		}
		if (Block.getIdFromBlock(p.worldObj.getBlock((int)p.posX, (int)p.posY, (int)p.posZ-1)) == Block.getIdFromBlock(Blocks.portal)) {
			return true;
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
    		NEED_LOGIN.add((EntityPlayerMP)event.player);
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
				event.sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§6Faça login/registro primeiro.")));
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
