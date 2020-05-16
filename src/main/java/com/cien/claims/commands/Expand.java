package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Expand extends CienCommandBase {

	public Expand() {
		super("expand", "Expande seu claim");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length < 1) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /expand <Tamanho>"));
		} else {
			int tamanho;
			try {
				tamanho = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Erro: "+ex.getMessage()));
				return;
			}
			com.cien.claims.Claim claimToExpand = CienClaims.CLAIMS.getClaimInside(player);
			if (claimToExpand == null) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Fique sobre um claim para expandir"));
				return;
			}
			if (!claimToExpand.getOwner().equals(player.getCommandSenderName())) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Você tem que ser dono do terreno para expandir"));
				return;
			}
			com.cien.claims.Claim claim = claimToExpand.expand(tamanho);
			if (claim.getLenght() > 150 || claim.getWidth() > 150) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Não pode ser maior que 150x150"));
				return;
			}
			if (claim.getLenght() < 10 || claim.getWidth() < 10) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Não pode ser menor que 10x10"));
				return;
			}
			for (com.cien.claims.Claim f:CienClaims.CLAIMS.getClaims()) {
				if (f != claimToExpand && (f.collidesWith(claim) || claim.collidesWith(f))) {
					player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Seu claim estaria em colisão com outro claim."));
					return;
				}
			}
			if (CienClaims.CLAIMS.getBlocksOf(player.getCommandSenderName()) - (claim.getSize()-claimToExpand.getSize()) < 0) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Blocos insuficientes. (Necessário "+(claim.getSize()-claimToExpand.getSize())+")"));
				return;
			}
			CienClaims.CLAIMS.removeClaim(claimToExpand);
			claimToExpand.undoFences();
			claimToExpand.getProperties().delete();
			CienClaims.CLAIMS.addClaim(claim);
			CienClaims.CLAIMS.removeBlocksOf(player.getCommandSenderName(), (claim.getSize()-claimToExpand.getSize()));
			claim.makeFencesAndSave();
			player.addChatMessage(Util.fixColors(Util.getPrefix()+"Claim Expandido Com Sucesso!"));
		}
	}

}
