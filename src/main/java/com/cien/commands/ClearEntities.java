package com.cien.commands;

import java.util.List;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class ClearEntities extends CienCommandBase {

	public ClearEntities() {
		super("clearentities", "Limpa todas as entidades.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!(sender instanceof DedicatedServer)) {
			if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.clearentities")) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
				return;
			}
		}
		int entities = 0;
		for (WorldServer w:Util.getWorlds()) {
			for (Chunk k:Util.getLoadedChunksOf(w)) {
				for (List<?> l:k.entityLists) {
					for (Object obj:l.toArray(new Object[l.size()])) {
						Entity t = (Entity) obj;
						t.setDead();
						entities++;
					}
				}
			}
		}
		sender.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Removido "+entities+" entidades.")));
	}

}
