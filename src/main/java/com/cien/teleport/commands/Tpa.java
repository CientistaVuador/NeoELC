package com.cien.teleport.commands;

import com.cien.CienCommandBase;
import com.cien.ScheduledTask;
import com.cien.Util;
import com.cien.data.Properties;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Tpa extends CienCommandBase {

	public Tpa() {
		super("tpa", "Envia um pedido de teleporte a um player.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /tpa <Player>")));
			return;
		}
		EntityPlayerMP tpPlayer = Util.getOnlinePlayerInexact(args[0]);
		if (tpPlayer == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Player offline ou inválido.")));
			return;
		}
		Properties prop = Properties.getProperties(tpPlayer.getCommandSenderName());
		Properties other = Properties.getProperties(player.getCommandSenderName());
		if (prop.getMemory("currentTpa") != null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Esse player já está recebendo um pedido de teleporte.")));
			return;
		}
		if (other.getMemory("enviandoTpa") != null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você já está enviando um pedido de teleporte.")));
			return;
		}
		other.setMemory("enviandoTpa", new Object());
		tpPlayer.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§a"+player.getCommandSenderName()+" §6Quer se teleportar até você, aceite com /tpac ou recuse com /tprc")));
		ScheduledTask pedido = Util.schedule("Tpa - "+player.getCommandSenderName()+" -> "+prop.getName(), () -> {
			if (prop.getMemory("currentTpa") == null) {
				return;
			}
			tpPlayer.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"§a"+player.getCommandSenderName()+" §6Quer se teleportar até você, aceite com /tpac ou recuse com /tprc")));
		}, 5*20);
		prop.setMemory("currentTpa", player.getCommandSenderName());
		Util.run("Tpa-Ignore - "+player.getCommandSenderName()+" -> "+prop.getName(), () -> {
			pedido.setComplete(true);
			if (prop.getMemory("currentTpa") != null) {
				tpPlayer.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Pedido de teleporte ignorado.")));
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Pedido de teleporte ignorado.")));
				other.setMemory("enviandoTpa", null);
				prop.setMemory("currentTpa", null);
			}
		}, 30*20);
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Pedido de teleporte enviado.")));
	}

}
