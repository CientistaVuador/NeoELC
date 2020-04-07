package com.cien.claims.commands;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class TrustList extends CienCommandBase {

	public TrustList() {
		super("trustlist", "Mostra a lista de trust no seu terreno.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		com.cien.claims.Claim f = CienClaims.CLAIMS.getClaimInside(player);
		if (f == null) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Você não está sobre nenhum claim.")));
			return;
		}
		if (!f.getOwner().equals(player.getCommandSenderName())) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"O Claim não é seu.")));
			return;
		}
		Map<String, Integer> map = f.getPlayersAndPermissionLevel();
		Set<Entry<String, Integer>> set = map.entrySet();
		if (set.size() == 0) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Não há ninguém com trust no seu claim.")));
			return;
		}
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Lista de Trust em "+f.getId()+":")));
		for (Entry<String, Integer> e:set) {
			String level = "Entrada";
			if (e.getValue() == com.cien.claims.Claim.TRUST) {
				level = "Trust";
			}
			player.addChatMessage(new ChatComponentText(Util.fixColors(" §6"+e.getKey()+", Permissão de "+level)));
		}
	}

}
