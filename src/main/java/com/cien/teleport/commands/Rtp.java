package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public class Rtp extends CienCommandBase {
	
	private final int[][] cachedLocations = new int[10][2];
	private int cacheIndex = 0;
	
	public Rtp() {
		super("rtp", "Teleporte aleatório.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Properties prop = Properties.getProperties(player.getCommandSenderName());
		String delay = prop.get("rtpDelay");
		if (delay != null && cacheIndex < cachedLocations.length) {
			long d = Long.parseLong(delay);
			if (d > System.currentTimeMillis()) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Aguarde...");
				return;
			}
		}
		if (player.worldObj.provider.dimensionId == 0) {
			WorldServer sv = (WorldServer) player.worldObj;
			if (cacheIndex >= 10) {
				int cache = (int) (Math.random() * cachedLocations.length);
				int[] pos = cachedLocations[cache];
				int x = pos[0];
				int z = pos[1];
				int y = Util.getHighestYAt(x, z, sv);
				Util.teleportPlayer(player, player.worldObj, x, y, z, player.rotationPitch, player.rotationYaw);
				Util.sendMessage(player, Util.getPrefix()+"Teleportado!");
				return;
			}
			int x = (int) ((30000 * Math.random()) - (30000/2f));
			int z = (int) ((30000 * Math.random()) - (30000/2f));
			int y = Util.getHighestYAt(x, z, sv);
			for (int i = 0; i < 10; i++) {
				int lastBlockID = Block.getIdFromBlock(sv.getBlock(x, y, z));
				if (lastBlockID != 8 && lastBlockID != 9) {
					break;
				}
				x = (int) ((30000 * Math.random()) - (30000/2f));
				z = (int) ((30000 * Math.random()) - (30000/2f));
				y = Util.getHighestYAt(x, z, sv);
			}
			sv.theChunkProviderServer.loadChunk(x/16, z/16);
			Util.teleportPlayer(player, sv, x, Util.getHighestYAt(x, z, sv), z, player.rotationPitch, player.rotationYaw);
			int imutX = x;
			int imutZ = z;
			Util.run("Reteleport Player", () -> {
				Util.teleportPlayer(player, sv, imutX, Util.getHighestYAt(imutX, imutZ, sv), imutZ, player.rotationPitch, player.rotationYaw);
			}, 20);
			Util.sendMessage(player, Util.getPrefix()+"Teleportado!");
			prop.set("rtpDelay", Long.toString(System.currentTimeMillis()+30*1000));
			
			//cache control
			if (cacheIndex < cachedLocations.length) {
				cachedLocations[cacheIndex][0] = x;
				cachedLocations[cacheIndex][1] = z;
				cacheIndex++;
			}
		} else {
			Util.sendMessage(player, Util.getErrorPrefix()+"Teleporte aleatório é apenas permitido no mundo normal.");
		}
	}

}
