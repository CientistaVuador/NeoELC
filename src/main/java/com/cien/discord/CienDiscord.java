package com.cien.discord;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class CienDiscord implements EventListener {

	public static final CienDiscord DISCORD = new CienDiscord();
	
	private final Map<String, Long> tokens = new HashMap<>();
	
	private final Map<Long, String> playerIdCache = new HashMap<>();
	private final Map<String, Long> idPlayerCache = new HashMap<>();
	
	private final Properties prop = Properties.getProperties("(Module)CienDiscord");
	
	private JDA jda = null;
	private String token = null;
	private boolean running = false;
	private String link = null;
	
	private long commandChatID = 0;
	private long staffChatID = 0;
	private long vipChatID = 0;
	private long globalChatID = 0;
	
	private CienDiscord() {
		this.token = prop.get("token");
		this.link = prop.get("link");
		String commandChat = prop.get("commandChat");
		String globalChat = prop.get("globalChat");
		String vipChat = prop.get("vipChat");
		String staffChat = prop.get("staffChat");
		if (commandChat != null) {
			this.commandChatID = Long.parseLong(commandChat);
		}
		if (globalChat != null) {
			this.globalChatID = Long.parseLong(globalChat);
		}
		if (vipChat != null) {
			this.vipChatID = Long.parseLong(vipChat);
		}
		if (staffChat != null) {
			this.staffChatID = Long.parseLong(staffChat);
		}
		
		Util.run("Server Started", () -> {
			CienDiscord.DISCORD.sendMessage(":white_check_mark: Servidor iniciou.");
			CienDiscord.DISCORD.setChannelsTopic("Servidor iniciou, aguarde a atualização do tópico.");
		}, 20);
		Util.schedule("Update Chat Description", () -> {
			if (!running) {
				return;
			}
			MinecraftServer server = MinecraftServer.getServer();
			String description = "Servidor Online -> "+server.getAllUsernames().length+"/"+server.getMaxPlayers()+" - TPS: "+Util.getTPS();
			TextChannel vip = getVipChat();
			TextChannel global = getGlobalChat();
			TextChannel staff = getStaffChat();
			TextChannel command = getCommandChat();
			if (vip != null) {
				vip.getManager().setTopic(description).queue();
			}
			if (global != null) {
				global.getManager().setTopic(description).queue();
			}
			if (staff != null) {
				staff.getManager().setTopic(description).queue();
			}
			if (command != null) {
				command.getManager().setTopic(description).queue();
			}
		}, 30*20);
	}
	
	public String generateToken(long discordID) {
		String token = UUID.randomUUID().toString();
		tokens.put(token, discordID);
		return token;
	}
	
	public Long consumeToken(String token) {
		return tokens.remove(token);
	}
	
	public boolean hasToken(long discordID) {
		return tokens.containsValue(discordID);
	}
	
	public void setChannelsTopic(String topic) {
		TextChannel vip = getVipChat();
		TextChannel global = getGlobalChat();
		TextChannel staff = getStaffChat();
		TextChannel command = getCommandChat();
		if (vip != null) {
			vip.getManager().setTopic(topic).queue();
		}
		if (global != null) {
			global.getManager().setTopic(topic).queue();
		}
		if (staff != null) {
			staff.getManager().setTopic(topic).queue();
		}
		if (command != null) {
			command.getManager().setTopic(topic).queue();
		}
	}
	
	public boolean hasDiscordID(String player) {
		return getDiscordID(player) != 0;
	}
	
	public User getUser(long id) {
		return jda.getUserById(id);
	}
	
	public long getDiscordID(String player) {
		Long cached = idPlayerCache.get(player);
		if (cached != null) {
			return cached;
		}
		Properties prop = Properties.getProperties(player);
		String id = prop.get("discordID");
		if (id == null) {
			return 0;
		}
		return Long.parseLong(id);
	}
	
	public String getPlayerNameByDiscordID(long id) {
		String cached = playerIdCache.get(id);
		if (cached != null) {
			return cached;
		}
		for (String s:Properties.getAllProperties()) {
			if (s.length() > 0) {
				if (s.charAt(0) != '(') {
					long idd = getDiscordID(s);
					if (id == idd) {
						playerIdCache.put(id, s);
						idPlayerCache.put(s, id);
						return s;
					}
				}
			}
		}
		return null;
	}
	
	public void setDiscordID(String player, long id) {
		Properties prop = Properties.getProperties(player);
		prop.set("discordID", Long.toString(id));
		if (id == 0) {
			playerIdCache.remove(id);
			idPlayerCache.remove(player);
		} else {
			playerIdCache.put(id, player);
			idPlayerCache.put(player, id);
		}
	}
	
	public User getUser(String player) {
		long id = getDiscordID(player);
		if (id == 0) {
			return null;
		}
		return getUser(id);
	}
	
	public String getDiscordInvite() {
		return link;
	}
	
	public void setDiscordInvite(String invite) {
		this.link = invite;
		prop.set("link", link);
	}
	
	public long getCommandChatID() {
		return commandChatID;
	}
	
	public long getGlobalChatID() {
		return globalChatID;
	}
	
	public long getStaffChatID() {
		return staffChatID;
	}
	
	public long getVipChatID() {
		return vipChatID;
	}
	
	public void sendGlobalMessage(String msg) {
		TextChannel global = getGlobalChat();
		if (global != null) {
			global.sendMessage(msg).queue();
		}
	}
	
	public void sendVipMessage(String msg) {
		TextChannel vip = getVipChat();
		if (vip != null) {
			vip.sendMessage(msg).queue();
		}
	}
	
	public void sendStaffMessage(String msg) {
		TextChannel staff = getStaffChat();
		if (staff != null) {
			staff.sendMessage(msg).queue();
		}
	}
	
	public void sendCommandMessage(String msg) {
		TextChannel command = getCommandChat();
		if (command != null) {
			command.sendMessage(msg).queue();
		}
	}
	
	public void sendMessage(String msg) {
		sendGlobalMessage(msg);
		sendCommandMessage(msg);
		sendVipMessage(msg);
		sendStaffMessage(msg);
	}
	
	public void setCommandChatID(long commandChatID) {
		this.commandChatID = commandChatID;
		prop.set("commandChat", Long.toString(commandChatID));
	}
	
	public void setGlobalChatID(long globalChatID) {
		this.globalChatID = globalChatID;
		prop.set("globalChat", Long.toString(globalChatID));
	}
	
	public void setStaffChatID(long staffChatID) {
		this.staffChatID = staffChatID;
		prop.set("staffChat", Long.toString(staffChatID));
	}
	
	public void setVipChatID(long vipChatID) {
		this.vipChatID = vipChatID;
		prop.set("vipChat", Long.toString(vipChatID));
	}
	
	public TextChannel getVipChat() {
		if (jda == null) {
			return null;
		}
		return jda.getTextChannelById(vipChatID);
	}
	
	public TextChannel getStaffChat() {
		if (jda == null) {
			return null;
		}
		return jda.getTextChannelById(staffChatID);
	}
	
	public TextChannel getGlobalChat() {
		if (jda == null) {
			return null;
		}
		return jda.getTextChannelById(globalChatID);
	}
	
	public TextChannel getCommandChat() {
		if (jda == null) {
			return null;
		}
		return jda.getTextChannelById(commandChatID);
	}
	
	public Properties getProp() {
		return prop;
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
		prop.set("token", token);
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void start() throws Exception {
		if (running) {
			return;
		}
		JDABuilder builder = new JDABuilder(this.token);
		jda = builder.build().awaitReady();
		jda.addEventListener(this);
		running = true;
	}
	
	public void shutdown() throws Exception {
		if (!running) {
			return;
		}
		jda.shutdownNow();
		jda = null;
		running = false;
	}
	
	public JDA getJDA() {
		return jda;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerEntersServer(PlayerLoggedInEvent event) {
		CienDiscord.DISCORD.sendMessage(":arrow_up_small: **"+event.player.getCommandSenderName()+" Entrou no servidor.**");
    }
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerExit(PlayerLoggedOutEvent event) {
		CienDiscord.DISCORD.sendMessage(":small_red_triangle_down: **"+event.player.getCommandSenderName()+" Saiu do servidor.**");
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerDies(LivingDeathEvent event) {
		if (event.entityLiving instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
			DamageSource source = event.source;
			if (source != null && source.damageType != null) {
		        IChatComponent c = source.func_151519_b(player);
		        sendMessage(":regional_indicator_f: **"+c.getUnformattedText()+".**");
			} else {
				sendMessage(":regional_indicator_f: **"+player.getCommandSenderName()+" Morreu de causas desconhecidas.**");
			}
		}
	}
	
	private String[] split(String s) {
		List<String> list = new ArrayList<>();
		StringBuilder b = new StringBuilder(64);
		for (char c:s.toCharArray()) {
			if (c == ' ') {
				list.add(b.toString());
				b.setLength(0);
				continue;
			}
			b.append(c);
		}
		if (b.length() > 0) {
			list.add(b.toString());
			b.setLength(0);
		}
		return list.toArray(new String[list.size()]);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onDiscordEvent(DiscordGenericEvent event) {
		GenericEvent e = event.getEvent();
		if (e instanceof MessageReceivedEvent) {
			MessageReceivedEvent message = (MessageReceivedEvent) e;
			if (message.getChannel() instanceof TextChannel) {
				TextChannel ch = (TextChannel) message.getChannel();
				User user = message.getAuthor();
				if (user.isBot()) {
					return;
				}
				String msg = message.getMessage().getContentDisplay();
				long id = ch.getIdLong();
				if (id == getCommandChatID()) {
					if (msg.startsWith("elc->")) {
						msg = msg.substring("elc->".length());
						if (msg.length() <= 0) {
							CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Digite elc->ajuda para ver os comandos.");
							return;
						}
						String[] split = split(msg);
						String commandName = split[0];
						String[] args = new String[split.length - 1];
						if (args.length > 0) {
							for (int i = 0; i < args.length; i++) {
								args[i] = split[i + 1];
							}
						}
						DiscordCommand command = DiscordCommandManager.getCommand(commandName);
						if (command == null) {
							CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Comando inválido, digite elc->ajuda para ver os comandos.");
							return;
						}
						System.out.println(user.getName()+" executou elc->"+command.getName());
						try {
							command.onCommand(user, message.getMessage(), args, CienDiscord.DISCORD.getPlayerNameByDiscordID(user.getIdLong()));
						} catch (Exception ex) {
							System.out.println("Erro ao executar o comando "+command.getName()+" para "+user.getName());
							ex.printStackTrace();
						}
					}
				} else if (id == getGlobalChatID()) {
					String playerName = getPlayerNameByDiscordID(user.getIdLong());
					if (playerName == null) {
						playerName = user.getName();
					}
					String cht = CienChat.CHAT.getGlobalChatMessageFor(playerName, "§8§l[§aDISCORD§8§l]", "", "", msg);
					for (EntityPlayerMP p:Util.getOnlinePlayers()) {
						if (CienPermissions.PERMISSIONS.hasPermission(p.getCommandSenderName(), "chat.global")) {
							Util.sendMessage(p, cht);
						}
					}
					System.out.println("[GLOBAL] [DISCORD] "+playerName+": "+msg);
				} else if (id == getStaffChatID()) {
					String playerName = getPlayerNameByDiscordID(user.getIdLong());
					if (playerName == null) {
						playerName = user.getName();
					}
					String cht = CienChat.CHAT.getStaffChatMesssageFor(playerName, "§8§l[§aDISCORD§8§l]", msg);
					for (EntityPlayerMP p:Util.getOnlinePlayers()) {
						if (CienPermissions.PERMISSIONS.hasPermission(p.getCommandSenderName(), "chat.staff")) {
							Util.sendMessage(p, cht);
						}
					}
					System.out.println("[STAFF] [DISCORD] "+playerName+": "+msg);
				} else if (id == getVipChatID()) {
					String playerName = getPlayerNameByDiscordID(user.getIdLong());
					if (playerName == null) {
						playerName = user.getName();
					}
					String cht = CienChat.CHAT.getVipChatMesssageFor(playerName, "§8§l[§aDISCORD§8§l]", msg);
					for (EntityPlayerMP p:Util.getOnlinePlayers()) {
						if (CienPermissions.PERMISSIONS.hasPermission(p.getCommandSenderName(), "chat.vip")) {
							Util.sendMessage(p, cht);
						}
					}
					System.out.println("[VIP] [DISCORD] "+playerName+": "+msg);
				}
			}
		}
	}
	
	@Override
	public void onEvent(GenericEvent arg0) {
		Util.run("Discord Event", () -> {
			MinecraftForge.EVENT_BUS.post(new DiscordGenericEvent(arg0));
		});
	}
	
}
