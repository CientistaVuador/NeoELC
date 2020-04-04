package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Tpac extends CienCommandBase {

	public Tpac() {
		super("tpac", "Aceita um pedido de teleporte");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Properties prop = Properties.getProperties(player.getCommandSenderName());
		String tpa = (String) prop.getMemory("currentTpa");
		if (tpa == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você não recebeu nenhum pedido de teleporte.")));
			return;
		}
		EntityPlayerMP tpaPlayer = Util.getOnlinePlayer(tpa);
		if (tpaPlayer == null) {
			Properties other = Properties.getProperties(tpa);
			other.setMemory("enviandoTpa", null);
			prop.setMemory("currentTpa", null);
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player offline")));
			return;
		}
		Properties other = Properties.getProperties(tpa);
		other.setMemory("enviandoTpa", null);
		prop.setMemory("currentTpa", null);
		tpaPlayer.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§aPedido de teleporte aceito.")));
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§aPedido de teleporte aceito.")));
		
		Util.teleportPlayer(tpaPlayer, player.worldObj, (float)player.posX, (float)player.posY, (float)player.posZ, player.rotationPitch, player.rotationYaw);
	}

}
