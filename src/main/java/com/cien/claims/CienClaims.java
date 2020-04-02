package com.cien.claims;

import java.util.ArrayList;
import java.util.List;

import com.cien.Util;
import com.cien.data.Properties;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class CienClaims {

	public static final CienClaims CLAIMS = new CienClaims();
	
	private final List<Claim> claims = new ArrayList<>();
	private final Properties prop = Properties.getProperties("(Module)CienClaims");
	
	private CienClaims() {
		Util.run("Claims Load", () -> {
			String[] names = Properties.getAllProperties();
			for (String name:names) {
				try {
					if (name.startsWith("(Claim)")) {
						claims.add(new Claim(Properties.getProperties(name)));
					}
				} catch (Exception ex) {
					System.out.println("Erro ao carregar claim -> "+name+" -> "+ex.getClass().getName()+": "+ex.getMessage());
				}
			}
		});
		Util.schedule("Dar Blocos", () -> {
			for (EntityPlayerMP player:Util.getOnlinePlayers()) {
				CienClaims.CLAIMS.addBlocksTo(player.getCommandSenderName(), 100);
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Você recebeu 100 blocos de claim por jogar no servidor.")));
			}
		}, 5*60*20);
	}
	
	public int nextID() {
		String id = prop.get("nextID");
		if (id == null) {
			prop.set("nextID", "1");
			return 0;
		}
		int i = Integer.parseInt(id);
		prop.set("nextID", Integer.toString(i+1));
		return i;
	}
	
	public void addClaim(Claim c) {
		claims.add(c);
	}
	
	public boolean removeClaim(Claim c) {
		return claims.remove(c);
	}
	
	public Claim[] getClaims() {
		return claims.toArray(new Claim[claims.size()]);
	}
	
	public Claim[] getClaims(String owner) {
		List<Claim> c = new ArrayList<>();
		for (Claim f:getClaims()) {
			if (f.getOwner().equals(owner)) {
				c.add(f);
			}
		}
		return c.toArray(new Claim[c.size()]);
	}
	
	public Claim getClaimInside(EntityPlayerMP player) {
		for (Claim f:getClaims()) {
			if (f.isInside(player)) {
				return f;
			}
		}
		return null;
	}
	
	public long getBlocksOf(String player) {
		Properties prop = Properties.getProperties(player);
		String blocks = prop.get("claimBlocks");
		if (blocks == null) {
			return 0;
		}
		try {
			return Long.parseLong(blocks);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}
	
	public void setBlocksOf(String player, long blocks) {
		Properties prop = Properties.getProperties(player);
		prop.set("claimBlocks", Long.toString(blocks));
	}
	
	public void addBlocksTo(String player, long blocks) {
		setBlocksOf(player, getBlocksOf(player)+blocks);
	}
	
	public void removeBlocksOf(String player, long blocks) {
		addBlocksTo(player, blocks * -1);
	}
	
	
	
}
