package com.cien.levels;

import com.cien.Module;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;
import com.cien.levels.commands.Level;
import com.cien.superchat.SuperChatProcessor;
import com.cien.superchat.SuperChatProcessorManager;
import com.cien.teleport.CienTeleport;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class CienLevels extends Module {

	public static final class SuperChatShowLevel extends SuperChatProcessor {

		public SuperChatShowLevel() {
			super("showlevel");
		}

		@Override
		public IChatComponent process(String[] args, String msg, String unformmated) {
			IChatComponent text = new ChatComponentText("[VerNivel]");
			ChatStyle style = new ChatStyle();
			style.setColor(EnumChatFormatting.GREEN);
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§aClique para ver seu nível")));
			style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/level"));
			text.setChatStyle(style);
			return text;
		}
		
	}
	
	public static final CienLevels LEVELS = new CienLevels();
	
	private CienLevels() {
		super("CienLevels");
	}
	
	@Override
	public void start() {
		SuperChatProcessorManager.addProcessor(new SuperChatShowLevel());
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new Level());
	}
	
	public int getLevelOf(String player) {
		Properties prop = Properties.getProperties(player);
		String level = prop.get("cienlevel");
		if (level == null) {
			return 0;
		}
		try {
			return Integer.parseInt(level);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}
	
	public void setLevelOf(String player, int level) {
		Properties prop = Properties.getProperties(player);
		prop.set("cienlevel", Integer.toString(level));
	}
	
	public void increaseLevel(String player) {
		setLevelOf(player, getLevelOf(player)+1);
	}
	
	public long getXPOf(String player) {
		Properties prop = Properties.getProperties(player);
		String level = prop.get("cienlevelxp");
		if (level == null) {
			return 0;
		}
		try {
			return Long.parseLong(level);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}
	
	public void setXPOf(String player, long xp) {
		Properties prop = Properties.getProperties(player);
		prop.set("cienlevelxp", Long.toString(xp));
	}
	
	public void addXPTo(String player, long xp) {
		setXPOf(player, getXPOf(player)+xp);
	}
	
	public long getNextLevelRequiredXPFor(String player) {
		int level = getLevelOf(player);
		long base = 50;
		for	(int i = 0; i < level; i++) {
			base += base/2;
		}
		return base;
	}
	
	public boolean canLevelUp(String player) {
		long required = getNextLevelRequiredXPFor(player);
		long current = getXPOf(player);
		if (current >= required) {
			return true;
		}
		return false;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
	public void onEntityDamage(LivingDeathEvent event) {
		Entity ent = event.entity;
		if (ent == null) {
			return;
		}
		if (ent instanceof EntityLiving) {
			EntityLiving living = (EntityLiving) ent;
			if (event.source.getSourceOfDamage() instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) event.source.getSourceOfDamage();
				if (player.getCommandSenderName() == null) {
					return;
				}
				for (char c:player.getCommandSenderName().toCharArray()) {
					if (c == '-') {
						return;
					}
					if (c == '[') {
						return;
					}
					if (c == ']') {
						return;
					}
				}
				long xp = (long) living.getMaxHealth();
				addXPTo(player.getCommandSenderName(), xp);
				if (canLevelUp(player.getCommandSenderName())) {
					increaseLevel(player.getCommandSenderName());
					LongDecimal money = LongDecimal.valueOf(getXPOf(player.getCommandSenderName())/10);
					Util.sendMessage(player, "§3Você upou para o nível "+getLevelOf(player.getCommandSenderName())+" +C$ "+money.toFormattedString()+" ~showlevel:");
					Util.sendMessage(player, "§3+Número de homes aumentada!");
					setXPOf(player.getCommandSenderName(), 0);
					CienEconomy.ECONOMY.addPlayerMoney(player.getCommandSenderName(), money);
					CienTeleport.TELEPORT.setMaxHomes(player.getCommandSenderName(), CienTeleport.TELEPORT.getMaxHomes(player.getCommandSenderName())+1);
				}
			}
		}
		
	}
	
}
