package com.cien.votifier.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.permissions.CienPermissions;
import com.cien.votifier.CienVotifier;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class Caixa extends CienCommandBase {

	public Caixa() {
		super("caixa", "Usa um vote e pega sua recompensa.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length < 1) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /caixa <itens/abrir/setitem/votos>");
			return;
		}
		String op = args[0].toLowerCase();
		if (op.equals("itens")) {
			ItemStack[] itens = CienVotifier.VOTIFIER.getItems();
			if (itens.length == 0) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Nenhum item foi adicionado.");
				return;
			}
			Util.sendMessage(player, Util.getPrefix()+"Itens: ");
			for (ItemStack s:itens) {
				Util.sendMessage(player, " §6"+s.getDisplayName()+" * "+s.stackSize);
			}
			return;
		}
		if (op.equals("setitem")) {
			if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.caixa")) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
				return;
			}
			if (args.length != 2) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /caixa setitem <nbt(true/false)>");
				return;
			}
			boolean nbt = Boolean.parseBoolean(args[1]);
			ItemStack hand = player.getCurrentEquippedItem();
			if (hand == null) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Coloque o item em sua mão.");
				return;
			}
			boolean result = CienVotifier.VOTIFIER.setItem(hand, nbt);
			if (result) {
				Util.sendMessage(player, Util.getPrefix()+"Item adicionado.");
			} else {
				Util.sendMessage(player, Util.getPrefix()+"Item removido.");
			}
			return;
		}
		if (op.equals("abrir")) {
			int votes = CienVotifier.VOTIFIER.getVoteNumberFor(player.getCommandSenderName());
			if (votes <= 0) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Você não tem votos.");
				return;
			}
			ItemStack[] itens = CienVotifier.VOTIFIER.getItems();
			if (itens.length == 0) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Nenhum item foi adicionado ao sistema de vote, seu voto não foi consumido.");
				return;
			}
			ItemStack[] randomized = new ItemStack[itens.length*2];
			for (int i = 0; i < randomized.length; i++) {
				randomized[i] = itens[(int) (Math.random() * itens.length)];
			}
			ItemStack choose = randomized[(int) (Math.random() * randomized.length)];
			Util.sendMessage(player, Util.getPrefix()+"Você ganhou "+choose.getDisplayName()+" * "+choose.stackSize);
			boolean b = player.inventory.addItemStackToInventory(choose);
			if (!b) {
				Util.sendMessage(player, Util.getErrorPrefix()+"Item dropado no chão.");
				player.entityDropItem(choose, 0.5f);
			}
			CienVotifier.VOTIFIER.setVoteNumberFor(player.getCommandSenderName(), votes - 1);
			return;
		}
		if (op.equals("votos")) {
			int votes = CienVotifier.VOTIFIER.getVoteNumberFor(player.getCommandSenderName());
			Util.sendMessage(player, Util.getPrefix()+"Você tem "+votes+" votos.");
			return;
		}
		Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /caixa <itens/abrir/setitem/votos>");
	}

}
