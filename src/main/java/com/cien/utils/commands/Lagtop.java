package com.cien.utils.commands;

import java.util.Arrays;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.claims.Claim;
import com.cien.utils.CienUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Lagtop extends CienCommandBase {

	
	public static class PlayerLagtop implements Comparable<PlayerLagtop> {

		private final EntityPlayerMP player;
		private final long time;
		
		public PlayerLagtop(EntityPlayerMP player, long time) {
			this.player = player;
			this.time = time;
		}
		
		public EntityPlayerMP getPlayer() {
			return player;
		}
		
		public long getTime() {
			return time;
		}
		
		@Override
		public int compareTo(PlayerLagtop o) {
			if (time < o.time) {
				return 1;
			}
			if (time > o.time) {
				return -1;
			}
			return 0;
		}
	}
	
	private static PlayerLagtop[] nonCurrentClaimCache = null;
	private static long nextUpdate = 0;
	
	public static PlayerLagtop[] getAllSorted(boolean currentClaim) {
		if (System.currentTimeMillis() < nextUpdate) {
			if (!currentClaim) {
				if (nonCurrentClaimCache != null) {
					return nonCurrentClaimCache.clone();
				}
			}
		}
		EntityPlayerMP[] online = Util.getOnlinePlayers();
		PlayerLagtop[] top = new PlayerLagtop[online.length];
		for (int i = 0; i < online.length; i++) {
			EntityPlayerMP on = online[i];
			if (currentClaim) {
				Claim c = CienClaims.CLAIMS.getClaimInside(on);
				if (c == null) {
					top[i] = new PlayerLagtop(on, 0);
				} else {
					top[i] = new PlayerLagtop(on, CienUtils.UTILS.getMediumTickTimeOf(c));
				}
			} else {
				Claim[] claims = CienClaims.CLAIMS.getClaims(on.getCommandSenderName());
				long time = 0;
				for (int f = 0; f < claims.length; f++) {
					time += CienUtils.UTILS.getMediumTickTimeOf(claims[f]);
				}
				if (time == 0 || claims.length == 0) {
					top[i] = new PlayerLagtop(on, 0);
				} else {
					top[i] = new PlayerLagtop(on, time/claims.length);
				}
			}
		}
		Arrays.sort(top);
		if (currentClaim) {
			return top;
		} else {
			nextUpdate = System.currentTimeMillis() + 3*60*1000;
			nonCurrentClaimCache = top;
			return nonCurrentClaimCache.clone();
		}
	}
	
	public Lagtop() {
		super("lagtop", "Mostra o top 10 players mais lagados.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /lagtop <claimAtual(true/false)>");
			return;
		}
		boolean atual = Boolean.parseBoolean(args[0]);
		PlayerLagtop[] sorted = getAllSorted(atual);
		if (sorted.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Não há ninguém online.");
		} else {
			Util.sendMessage(player, "§6Top 10 Lag:");
			int lenght = sorted.length;
			if (lenght > 10) {
				lenght = 10;
			}
			for (int i = 0; i < lenght; i++) {
				PlayerLagtop f = sorted[i];
				Util.sendMessage(player, " §6"+f.getPlayer().getCommandSenderName()+" - "+f.getTime()+" nanos/tile");
			}
		}
	}

}
