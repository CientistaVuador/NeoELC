package com.cien.claims;

import com.cien.data.Properties;

public class CienClaims {

	public static final CienClaims CLAIMS = new CienClaims();
	
	private CienClaims() {
		
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
