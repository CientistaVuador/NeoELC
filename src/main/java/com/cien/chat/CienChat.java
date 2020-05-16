package com.cien.chat;

import java.util.ArrayList;
import java.util.List;

import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.discord.CienDiscord;
import com.cien.login.CienLogin;
import com.cien.permissions.CienPermissions;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;

public class CienChat {
	
	public static final CienChat CHAT = new CienChat();
	
	private CienChat() {
		System.out.println("CienChat Iniciado!");
	}
	
	public long getMutedTimeLeft(String player) {
		Properties prop = Properties.getProperties(player);
		String time = prop.get("muteTime");
		if (time == null) {
			return 0;
		}
		try {
			long left = Long.parseLong(time)-System.currentTimeMillis();
			if (left < 0) {
				return 0;
			}
			return left;
		} catch (NumberFormatException ex) {
			return 0;
		}
	}
	
	public void setLastSenderFor(String player, String sender) {
		Properties prop = Properties.getProperties(player);
		prop.setMemory("lastSender", sender);
	}
	
	public String getLastSenderFor(String player) {
		Properties prop = Properties.getProperties(player);
		Object obj = prop.getMemory("lastSender");
		if (obj == null) {
			return null;
		}
		return (String)obj;
	}
	
	public void setMutedTimeLeft(String player, long time) {
		Properties prop = Properties.getProperties(player);
		prop.set("muteTime", Long.toString(time+System.currentTimeMillis()));
	}
	
	public String getGlobalChatMessageFor(String player, String prefix1, String prefix2, String prefix3, String msg) {
		return "§7[G] §f"+prefix1.replace('&', '§')+"§f"+prefix2.replace('&', '§')+"§f"+prefix3.replace('&', '§')+"§f"+player.replace('&', '§')+"§7: "+msg;
	}
	
	public String getLocalChatMesssageFor(String player, String prefix1, String prefix2, String prefix3, String msg) {
		return "§e[L] §f"+prefix1.replace('&', '§')+"§f"+prefix2.replace('&', '§')+"§f"+prefix3.replace('&', '§')+"§f"+player.replace('&', '§')+"§e: "+msg;
	}
	
	public String getVipChatMesssageFor(String player, String prefix1, String msg) {
		return "§6§l[§eVIP§6§l] §f"+prefix1.replace('&', '§')+"§f"+player.replace('&', '§')+"§6: "+msg;
	}
	
	public String getStaffChatMesssageFor(String player, String prefix1, String msg) {
		return "§2§l[§aSTAFF§2§l] §f"+prefix1.replace('&', '§')+"§f"+player.replace('&', '§')+"§a: "+msg;
	}
	
	public String getPrivateChatMessageForReceiver(String sender, String msg) {
		return "§2De §f"+sender+"§2: "+msg;
	}
	
	public String getPrivateChatMessageForSender(String receiver, String msg) {
		return "§2Para §f"+receiver+"§2: "+msg;
	}
	
	public String buildVipChatMessageFor(String player, String msg) {
		String prefix1 = CienPermissions.PERMISSIONS.getGroupPrefixOf(player);
		if (prefix1 == null) {
			prefix1 = "";
		}
		return getVipChatMesssageFor(getPlayerNick(player), prefix1, msg);
	}
	
	public String buildStaffChatMessageFor(String player, String msg) {
		String prefix1 = CienPermissions.PERMISSIONS.getGroupPrefixOf(player);
		if (prefix1 == null) {
			prefix1 = "";
		}
		return getStaffChatMesssageFor(getPlayerNick(player), prefix1, msg);
	}
	
	public String buildGlobalChatMessageFor(String player, String msg) {
		Properties prop = Properties.getProperties(player);
		String prefix1 = CienPermissions.PERMISSIONS.getGroupPrefixOf(player);
		if (prefix1 == null) {
			prefix1 = "";
		}
		String prefix2 = prop.get("prefix2");
		if (prefix2 == null) {
			prefix2 = "";
		}
		String prefix3 = prop.get("prefix3");
		if (prefix3 == null) {
			prefix3 = "";
		}
		return getGlobalChatMessageFor(getPlayerNick(player), prefix1, prefix2, prefix3, msg);
	}
	
	public String buildLocalChatMessageFor(String player, String msg) {
		Properties prop = Properties.getProperties(player);
		String prefix1 = CienPermissions.PERMISSIONS.getGroupPrefixOf(player);
		if (prefix1 == null) {
			prefix1 = "";
		}
		String prefix2 = prop.get("prefix2");
		if (prefix2 == null) {
			prefix2 = "";
		}
		String prefix3 = prop.get("prefix3");
		if (prefix3 == null) {
			prefix3 = "";
		}
		return getLocalChatMesssageFor(getPlayerNick(player), prefix1, prefix2, prefix3, msg);
	}
	
	public void setPlayerNick(String player, String nick) {
		Properties prop = Properties.getProperties(player);
		prop.set("fakeNick", nick);
	}
	
	public String getPlayerNick(String player) {
		Properties prop = Properties.getProperties(player);
		String nick = prop.get("fakeNick");
		if (nick == null) {
			return player;
		}
		return nick;
	}
	
	public void setPlayerPrefix(String player, String prefix) {
		Properties prop = Properties.getProperties(player);
		prop.set("prefix2", prefix);
	}
	
	public String getPlayerPrefix(String player) {
		Properties prop = Properties.getProperties(player);
		return prop.get("prefix2");
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onChatMessage(ServerChatEvent event) {
		event.setCanceled(true);
		if (CienLogin.LOGIN.shouldBeFreezed(event.player.getCommandSenderName())) {
			Util.sendMessage(event.player, Util.getErrorPrefix()+"Faça login primeiro antes de falar no chat local.");
			return;
		}
		String msg = CienChat.CHAT.buildLocalChatMessageFor(event.player.getCommandSenderName(), event.message);
		if (CienPermissions.PERMISSIONS.hasPermission(event.player.getCommandSenderName(), "chat.colors")) {
			msg = msg.replace('&', '§');
		}
		int dim = event.player.dimension;
		PositiveLocation loc = new PositiveLocation((int)event.player.posX, (int)event.player.posY, (int)event.player.posZ);
		List<EntityPlayerMP> notReceived = new ArrayList<>();
		for (EntityPlayerMP player:Util.getOnlinePlayers()) {
			PositiveLocation loc2 = new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ);
			if (player.dimension == dim) {
				int distance = loc.distanceXZ(loc2);
				if (distance <= 100) {
					Util.sendMessage(player, msg);
					continue;
				}
			}
			notReceived.add(player);
		}
		for (EntityPlayerMP p:notReceived) {
			if (CienPermissions.PERMISSIONS.hasPermission(p.getCommandSenderName(), "chat.staff")) {
				Util.sendMessage(p, "§7[LOCAL] "+event.player.getCommandSenderName()+": "+event.message);
			}
		}
		System.out.println("[LOCAL] "+event.player.getCommandSenderName()+": "+event.message);
		String prefix = CienPermissions.PERMISSIONS.getGroupPrefixOf(event.player.getCommandSenderName());
		if (prefix == null) {
			prefix = "";
		}
		prefix = prefix.replace('&', '§');
		CienDiscord.DISCORD.sendStaffMessage("[LOCAL] "+Util.discordColorsToBlackAndWhite(prefix)+" "+event.player.getCommandSenderName()+": "+Util.discordColorsToBlackAndWhite(event.message.replace('&', '§')));
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onCommand(CommandEvent event) {
		if (event.command.getCommandName().equalsIgnoreCase("tell")) {
			Util.sendMessage(event.sender, Util.getErrorPrefix()+"Use /p <Player> <Mensagem> ao invés de /tell");
			event.setCanceled(true);
		}
	}
}
