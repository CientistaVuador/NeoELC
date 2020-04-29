package com.cien.vip.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import com.cien.vip.CienVIP;
import com.cien.vip.Key;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class GerarKey extends CienCommandBase {

	public GerarKey() {
		super("gerarkey", "Gera uma key de vip.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.gerarkey")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
			return;
		}
		if (args.length != 1) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /gerarkey <Tempo em Segs(-1 para Infinito)>");
		} else {
			long time;
			try {
				time = Long.parseLong(args[0]) * 1000;
			} catch (NumberFormatException ex) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
				return;
			}
			boolean infinity = time < 0;
			if (CienVIP.VIP.getVipGroup() == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"O Grupo de permissão vip não foi definido.");
				return;
			}
			Key k = CienVIP.VIP.generateKey(time, infinity);
			Util.sendMessage(player, Util.getPrefix()+"Key: "+k.getToken());
		}
	}

}
