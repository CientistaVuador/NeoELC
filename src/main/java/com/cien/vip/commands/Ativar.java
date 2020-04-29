package com.cien.vip.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.discord.CienDiscord;
import com.cien.vip.CienVIP;
import com.cien.vip.Key;
import com.cien.vip.VipActivationEvent;

import net.dv8tion.jda.api.entities.User;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public class Ativar extends CienCommandBase {

	public Ativar() {
		super("ativar", "Ativa uma key.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length != 1) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /ativar <Key>");
			return;
		}
		String key = args[0].toUpperCase();
		if (CienVIP.VIP.isVip(player.getCommandSenderName())) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Você já é vip.");
			return;
		}
		if (CienVIP.VIP.getVipGroup() == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Nenhum grupo vip foi definido.");
			return;
		}
		if (CienVIP.VIP.getVipInfinityRole() == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Nenhum role de vip infinito foi definido.");
			return;
		}
		if (CienVIP.VIP.getVipRole() == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Nenhum role de vip foi definido.");
			return;
		}
		User s = CienDiscord.DISCORD.getUser(player.getCommandSenderName());
		if (s == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Você não está registrado no discord, vá até o chat de comandos do CienBot e digite elc->register");
			return;
		}
		Key k = CienVIP.VIP.tryConsumeKeyAndApplyGroup(key, player.getCommandSenderName());
		if (k == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Key Inválida.");
			return;
		}
		Util.sendMessage(player, Util.getPrefix()+"Vip Ativado!");
		MinecraftForge.EVENT_BUS.post(new VipActivationEvent(player, k));
	}

}
