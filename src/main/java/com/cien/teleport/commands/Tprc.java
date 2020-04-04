package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Tprc extends CienCommandBase {

	public Tprc() {
		super("tprc", "Recusa um pedido de teleporte");
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
		tpaPlayer.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Pedido de teleporte recusado.")));
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Pedido de teleporte recusado.")));
	}

}
