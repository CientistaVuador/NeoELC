package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.data.Properties;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class MoveClaim extends CienCommandBase {

	public MoveClaim() {
		super("moveclaim", "Move os blocos de um claim para outro.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length < 2) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /moveclaim <Claim A> <Claim B>");
			return;
		}
		Properties prop = Properties.getProperties(player.getCommandSenderName());
		Object coolDown = prop.getMemory("moveclaim_cooldown");
		if (coolDown != null) {
			long co = (long) coolDown;
			if (System.currentTimeMillis() < co) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Aguarde...");
				return;
			}
		}
		int idA;
		int idB;
		try {
			idA = Integer.parseInt(args[0]);
			idB = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
			return;
		}
		com.cien.claims.Claim a = CienClaims.CLAIMS.getClaim(idA);
		com.cien.claims.Claim b = CienClaims.CLAIMS.getClaim(idB);
		if (a == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Claim A é inválido.");
			return;
		}
		if (b == null) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Claim B é inválido.");
			return;
		}
		if (!a.getOwner().equals(player.getCommandSenderName())) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Você não é dono do claim A");
			return;
		}
		if (!b.getOwner().equals(player.getCommandSenderName())) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Você não é dono do claim B");
			return;
		}
		if (a.getLenght() > 50 || a.getWidth() > 50) {
			Util.sendMessage(player, Util.getErrorPrefix()+"O Claim não pode ser maior que 50x50");
			return;
		}
		if (b.getLenght() > 50 || b.getWidth() > 50) {
			Util.sendMessage(player, Util.getErrorPrefix()+"O Claim não pode ser maior que 50x50");
			return;
		}
		if (args.length == 2) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Atenção: Mover claims pode causar vários bugs e não nos responsabilizamos por perda de itens, confirme clicando aqui -> ~command:/moveclaim,"+a.getId()+","+b.getId()+",confirmar");
			return;
		}
		if (args[2].equalsIgnoreCase("confirmar")) {
			Util.sendMessage(player, Util.getPrefix()+"Movendo...");
			boolean moved = a.moveBlocksToAndBack(b);
			if (moved) {
				Util.sendMessage(player, Util.getPrefix()+"Movido com Sucesso!");
				prop.setMemory("moveclaim_cooldown", System.currentTimeMillis()+5*60*1000);
			} else {
				Util.sendMessage(player, Util.getErrorPrefix()+"Não foi possível mover, os claims possuem tamanhos diferentes ou o mundo de um dos dois está descarregado.");
			}
		} else {
			Util.sendMessage(player, Util.getErrorPrefix()+"Confirmação inválida.");
		}
	}

}
