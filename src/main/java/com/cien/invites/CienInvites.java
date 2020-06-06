package com.cien.invites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cien.Module;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.discord.CienDiscord;
import com.cien.economy.CienEconomy;
import com.cien.economy.LongDecimal;
import com.cien.invites.commands.Invite;
import com.cien.votifier.CienVotifier;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;

public class CienInvites extends Module {

	public static final CienInvites INVITES = new CienInvites();
	
	private Properties prop = Properties.getProperties("(Module)CienInvites");
	private Map<String, List<String>> invites = new HashMap<>();
	
	public CienInvites() {
		super("CienInvites");
	}

	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new Invite());
	}
	
	@Override
	public void start() {
		for (Entry<String, String> e:prop.getEntries()) {
			try {
				String[] arr = prop.getArray(e.getKey());
				if (arr == null) {
					continue;
				}
				if (arr.length == 0) {
					continue;
				}
				List<String> list = new ArrayList<>(Arrays.asList(arr));
				invites.put(e.getKey(), list);
			} catch (Exception ex) {
				System.out.println("Erro ao carregar invites de "+e.getKey());
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void tick() {
		
	}
	
	private void save() {
		for (Entry<String, List<String>> e:invites.entrySet()) {
			if (e.getKey() == null || e.getValue() == null) {
				continue;
			}
			String[] invi = e.getValue().toArray(new String[e.getValue().size()]);
			prop.setArray(e.getKey(), invi);
		}
	}
	
	public String[] getInvitesOf(String player) {
		List<String> inv = invites.get(player);
		if (inv == null) {
			return new String[0];
		}
		return inv.toArray(new String[inv.size()]);
	}
	
	public String getInviteWith(String invitePlayer) {
		for (Entry<String, List<String>> e:invites.entrySet()) {
			if (e.getKey() == null || e.getValue() == null) {
				continue;
			}
			if (e.getValue().contains(invitePlayer)) {
				return e.getKey();
			}
 		}
		return null;
	}
	
	public boolean addInviteTo(String player, String invitePlayer) {
		List<String> inv = invites.get(player);
		if (inv == null) {
			inv = new ArrayList<String>();
			invites.put(player, inv);
		}
		if (inv.contains(invitePlayer)) {
			return false;
		}
		inv.add(invitePlayer);
		save();
		return true;
	}
	
	public boolean removeInviteOf(String player, String invitePlayer) {
		List<String> inv = invites.get(player);
		if (inv == null) {
			return false;
		}
		boolean b = inv.remove(invitePlayer);
		if (b) {
			save();
		}
		return b;
	}
	
	public String getIPOf(String player) {
		Properties prop = Properties.getProperties(player);
		return prop.get("lastIp");
	}
	
	public boolean alreadyJoinedServer(String player) {
		Properties prop = Properties.getProperties(player);
		return prop.get("AlreadyJoined") != null;
	}
	
	@SubscribeEvent
	public void onNewPlayerEntersServer(PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			Properties prop = Properties.getProperties(event.player.getCommandSenderName());
			EntityPlayerMP p = (EntityPlayerMP) event.player;
			prop.set("lastIp", p.getPlayerIP());
			if (prop.get("AlreadyJoined") == null) {
				Util.sendMessageToEveryone(Util.getPrefix()+p.getCommandSenderName()+" Entrou no servidor pela primeira vez!");
				CienDiscord.DISCORD.sendMessage(":tada: "+p.getCommandSenderName()+" Entrou no servidor pela primeira vez!");
				String invitedBy = getInviteWith(p.getCommandSenderName());
				if (invitedBy != null) {
					removeInviteOf(invitedBy, p.getCommandSenderName());
					String invitedIp = getIPOf(p.getCommandSenderName());
					String invitedByIp = getIPOf(invitedBy);
					boolean accept = true;
					if (invitedByIp != null && invitedIp != null) {
						if (invitedByIp.equals(invitedIp)) {
							accept = false;
						}
					}
					if (accept) {
						CienVotifier.VOTIFIER.setVoteNumberFor(invitedBy, CienVotifier.VOTIFIER.getVoteNumberFor(invitedBy)+3);
						CienEconomy.ECONOMY.addPlayerMoney(invitedBy, LongDecimal.valueOf(2500));
						Util.sendMessageToEveryone(Util.getPrefix()+p.getCommandSenderName()+" Foi convidado por "+invitedBy+" e como recompensa "+invitedBy+" recebeu 3 Votos & C$ 2,5K convide seus amigos com /invite !");
					}
				}
			}
			prop.set("AlreadyJoined", "yes");
		}
	}
}
