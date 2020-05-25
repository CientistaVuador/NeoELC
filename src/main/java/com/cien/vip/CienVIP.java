package com.cien.vip;

import java.util.ArrayList;
import java.util.List;
import com.cien.Module;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.data.Properties;
import com.cien.discord.CienDiscord;
import com.cien.discord.DiscordCommandManager;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;
import com.cien.permissions.CienPermissions;
import com.cien.vip.discordcommands.GetKey;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.minecraftforge.common.MinecraftForge;

public class CienVIP extends Module {
	public static final CienVIP VIP = new CienVIP();
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	
	private static String random() {
		StringBuilder alphabetBuilder = new StringBuilder(alphabet.length()*2);
		for (int i = 0; i < alphabet.length()*2; i++) {
			char c = alphabet.charAt((int)(Math.random() * alphabet.length()));
			alphabetBuilder.append(c);
		}
		String dic = alphabetBuilder.toString();
		StringBuilder random = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			char c = dic.charAt((int)(Math.random() * dic.length()));
			random.append(c);
		}
		return random.toString();
	}
	
	private final Properties prop = Properties.getProperties("(Module)CienVIP");
	
	private final List<Key> keys = new ArrayList<>();
	private String vipGroup = null;
	private long vipRoleDiscord = 0;
	private long vipInfinityRoleDiscord = 0;
	
	
	private CienVIP() {
		super("CienVIP");
	}
	
	@Override
	public void start() {
		String[] k = prop.getArray("keys");
		for (String s:k) {
			keys.add(Key.fromString(s));
		}
		this.vipGroup = prop.get("vipGroup");
		
		String vipInfinityRole = prop.get("vipInfinityRole");
		String vipRole = prop.get("vipRole");
		if (vipInfinityRole != null) {
			this.vipInfinityRoleDiscord = Long.parseLong(vipInfinityRole);
		}
		if (vipRole != null) {
			this.vipRoleDiscord = Long.parseLong(vipRole);
		}
		run(new ModuleRunnable() {
			@Override
			public void run(Module mdl, ModuleRunnable r) {
				for (String s:Properties.getAllProperties()) {
					if (s.length() > 0) {
						if (s.charAt(0) != '(') {
							if (CienVIP.VIP.isVip(s)) {
								if (!CienVIP.VIP.isVipInfinity(s)) {
									long time = CienVIP.VIP.getTimeLeft(s);
									if (time <= 0) {
										CienPermissions.PERMISSIONS.setGroup(s, CienPermissions.PERMISSIONS.getDefaultGroup());
										MinecraftForge.EVENT_BUS.post(new VipDeactivationEvent(s));
									}
								}
							}
						}
					}
				}
			}
		}, 60*20, true);
		DiscordCommandManager.register(new GetKey());
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new com.cien.vip.commands.Ativar());
		event.registerServerCommand(new com.cien.vip.commands.GerarKey());
		event.registerServerCommand(new com.cien.vip.commands.Vip());
	}
	
	public String getVipGroup() {
		return vipGroup;
	}
	
	public long getVipInfinityRoleDiscordID() {
		return vipInfinityRoleDiscord;
	}
	
	public long getVipRoleDiscordID() {
		return vipRoleDiscord;
	}
	
	public void setVipGroup(String vipGroup) {
		this.vipGroup = vipGroup;
		prop.set("vipGroup", vipGroup);
	}
	
	public void setVipInfinityRoleDiscordID(long vipInfinityRoleDiscord) {
		this.vipInfinityRoleDiscord = vipInfinityRoleDiscord;
		prop.set("vipInfinityRole", Long.toString(vipInfinityRoleDiscord));
	}
	
	public void setVipRoleDiscord(long vipRoleDiscord) {
		this.vipRoleDiscord = vipRoleDiscord;
		prop.set("vipRole", Long.toString(vipRoleDiscord));
	}
	
	public Role getVipRole() {
		JDA jda = CienDiscord.DISCORD.getJDA();
		if (jda == null) {
			return null;
		}
		return jda.getRoleById(vipRoleDiscord);
	}
	
	public Role getVipInfinityRole() {
		JDA jda = CienDiscord.DISCORD.getJDA();
		if (jda == null) {
			return null;
		}
		return jda.getRoleById(this.vipInfinityRoleDiscord);
	}
	
	public boolean isVip(String player) {
		String gp = CienPermissions.PERMISSIONS.getGroup(player);
		if (gp == null) {
			return false;
		}
		if (gp.equals(vipGroup)) {
			return true;
		}
		return false;
	}
	
	public long getTimeLeft(String player) {
		if (isVip(player)) {
			Properties prop = Properties.getProperties(player);
			String timeLeft = prop.get("vipTime");
			if (timeLeft == null) {
				return 0;
			}
			return Long.parseLong(timeLeft) - System.currentTimeMillis();
		}
		return 0;
	}
	
	public boolean isVipInfinity(String player) {
		if (isVip(player)) {
			Properties prop = Properties.getProperties(player);
			String infi = prop.get("vipInfinity");
			if (infi != null) {
				return Boolean.parseBoolean(infi);
			}
			return false;
		}
		return false;
	}
	
	private void saveKeys() {
		List<String> sv = new ArrayList<>();
		for (Key k:getKeys()) {
			sv.add(Key.toString(k));
		}
		prop.setArray("keys", sv.toArray(new String[sv.size()]));
	}
	
	public Key[] getKeys() {
		return keys.toArray(new Key[keys.size()]);
	}
	
	public Key generateKey(long time, boolean infinity) {
		Key k;
		if (infinity) {
			k = new Key(random(), -1);
		} else {
			k = new Key(random(), time);
		}
		keys.add(k);
		saveKeys();
		return k;
	}
	
	public Key consumeKey(String token) {
		for (Key k:getKeys()) {
			if (k.getToken().equals(token)) {
				keys.remove(k);
				saveKeys();
				return k;
			}
		}
		return null;
	}
	
	public String getUsedToken(String player) {
		Properties prop = Properties.getProperties(player);
		return prop.get("usedKey");
	}
	
	public Key tryConsumeKeyAndApplyGroup(String token, String player) {
		if (isVip(player)) {
			return null;
		}
		if (vipGroup == null) {
			return null;
		}
		Key k = consumeKey(token);
		if (k != null) {
			CienPermissions.PERMISSIONS.setGroup(player, vipGroup);
			Properties prop = Properties.getProperties(player);
			prop.set("vipInfinity", Boolean.toString(k.isInfinity()));
			prop.set("vipTime", Long.toString(System.currentTimeMillis()+k.getTime()));
			prop.set("usedKey", k.getToken());
			return k;
		}
		return null;
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onVipActivated(VipActivationEvent event) {
		Util.sendMessageToEveryone(Util.getPrefix()+event.getPlayer().getCommandSenderName()+" ATIVOU O VIP!");
		CienEconomy.ECONOMY.addPlayerMoney(event.getPlayer().getCommandSenderName(), LongDecimal.valueOf(1000000));
		CienClaims.CLAIMS.addBlocksTo(event.getPlayer().getCommandSenderName(), 1000000);
		JDA jda = CienDiscord.DISCORD.getJDA();
		if (jda != null) {
			User s = CienDiscord.DISCORD.getUser(event.getPlayer().getCommandSenderName());
			if (s != null) {
				Role l;
				if (event.getKey().isInfinity()) {
					l = CienVIP.VIP.getVipInfinityRole();
				} else {
					l = CienVIP.VIP.getVipRole();
				}
				if (l != null) {
					CienDiscord.DISCORD.getGuild().addRoleToMember(s.getIdLong(), l).queue();
					Util.sendMessage(event.getPlayer(), Util.getPrefix()+"Você foi adicionado em seu cargo no discord com sucesso!");
					return;
				}
			}
		}
		Util.sendMessage(event.getPlayer(), Util.getErrorPrefix()+"Aviso: Não foi possível adicionar você ao cargo do discord, consulte os administradores para receber.");
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onVipDeactivated(VipDeactivationEvent event) {
		Util.sendMessageToEveryone(Util.getPrefix()+"O vip de "+event.getPlayer()+" acabou.");
		JDA jda = CienDiscord.DISCORD.getJDA();
		if (jda != null) {
			User s = CienDiscord.DISCORD.getUser(event.getPlayer());
			if (s != null) {
				Role l = CienVIP.VIP.getVipRole();
				if (l != null) {
					CienDiscord.DISCORD.getGuild().removeRoleFromMember(s.getIdLong(), l).queue();
					return;
				}
			}
		}
		System.out.println("Não foi possível retirar o cargo vip de "+event.getPlayer());
	}
}
