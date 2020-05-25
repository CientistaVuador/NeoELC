package com.cien.banitem;

import java.util.ArrayList;
import java.util.List;

import com.cien.Module;
import com.cien.Util;
import com.cien.banitem.commands.BanItem;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class CienBanItem extends Module {
	public static final CienBanItem BANITEM = new CienBanItem();
	
	private final Properties prop = Properties.getProperties("(Module)CienBanItem");
	
	private final List<String> blockInteraction = new ArrayList<>();
	private final List<String> itemInteraction = new ArrayList<>();
	
	private CienBanItem() {
		super("CienBanItem");
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new BanItem());
	}
	
	public Properties getProperties() {
		return prop;
	}
	
	private void saveBlockInteraction() {
		prop.setArray("blockInteraction", getBlockInteractionBans());
	}
	
	public void setBlockInteractionBanFor(String block, boolean b) {
		if (!b) {
			blockInteraction.remove(block);
			saveBlockInteraction();
		} else {
			if (!blockInteraction.contains(block)) {
				blockInteraction.add(block);
				saveBlockInteraction();
			}
		}
	}
	
	public boolean getBlockInteractionBanFor(String block) {
		return blockInteraction.contains(block);
	}
	
	public String[] getBlockInteractionBans() {
		return blockInteraction.toArray(new String[blockInteraction.size()]);
	}
	
	private void saveItemInteraction() {
		prop.setArray("itemInteraction", getBlockInteractionBans());
	}
	
	public void setItemInteractionBanFor(String item, boolean b) {
		if (!b) {
			itemInteraction.remove(item);
			saveItemInteraction();
		} else {
			if (!itemInteraction.contains(item)) {
				itemInteraction.add(item);
				saveItemInteraction();
			}
		}
	}
	
	public boolean getItemInteractionBanFor(String item) {
		return itemInteraction.contains(item);
	}
	
	public String[] getItemInteractionBans() {
		return itemInteraction.toArray(new String[itemInteraction.size()]);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onBlockInteract(PlayerInteractEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
		if (player != null) {
			if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
				WorldServer sv = (WorldServer) player.worldObj;
				String name = Util.getItemNameID(Item.getItemFromBlock(sv.getBlock(event.x, event.y, event.z)));
				int meta = sv.getBlockMetadata(event.x, event.y, event.z);
				if (getBlockInteractionBanFor(name+":*") || getBlockInteractionBanFor(name+":"+meta)) {
					String perm1 = "block."+name+":"+meta;
					String perm2 = "block."+name+":*";
					if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), perm1) && !CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), perm2)) {
						event.setCanceled(true);
						Util.sendMessage(player, Util.getErrorPrefix()+"Você não pode usar este bloco. ("+perm1+", "+perm2+")");
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onItemInteract(PlayerInteractEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
		if (player != null) {
			ItemStack hand = player.getCurrentEquippedItem();
			if (hand == null) {
				return;
			}
			String name = Util.getItemNameID(hand.getItem());
			int meta = hand.getItemDamage();
			if (getItemInteractionBanFor(name+":*") || getItemInteractionBanFor(name+":"+meta)) {
				String perm1 = "item."+name+":"+meta;
				String perm2 = "item."+name+":*";
				if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), perm1) && !CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), perm2)) {
					event.setCanceled(true);
					Util.sendMessage(player, Util.getErrorPrefix()+"Você não pode usar este item. ("+perm1+", "+perm2+")");
				}
			}
		}
	}
}
