package com.cien.claims.commands;

import com.cien.CienCommandBase;
import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.data.Properties;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class Claim extends CienCommandBase {

	public Claim() {
		super("claim", "Cria um claim");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Properties prop = Properties.getProperties(player.getCommandSenderName());
		com.cien.claims.Claim claim;
		if (args.length == 0) {
			PositiveLocation loc1 = (PositiveLocation) prop.getMemory("pos1");
			PositiveLocation loc2 = (PositiveLocation) prop.getMemory("pos2");
			if (loc1 == null && loc2 == null) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Uso: /claim <Tamanho> ou marque as posições com /pos1 e /pos2")));
				return;
			} else {
				claim = new com.cien.claims.Claim(loc1, loc2, player.worldObj.provider.getDimensionName(), player.getCommandSenderName(), CienClaims.CLAIMS.nextID());
			}
		} else {
			int tamanho;
			try {
				tamanho = Integer.parseInt(args[0]);
				if (tamanho < 0) {
					throw new NumberFormatException("Não pode ser negativo.");
				}
			} catch (NumberFormatException ex) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Erro: "+ex.getMessage())));
				return;
			}
			claim = new com.cien.claims.Claim(new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ), player.worldObj.provider.getDimensionName(), player.getCommandSenderName(), CienClaims.CLAIMS.nextID(), tamanho, tamanho);
		}
		if (claim.getLenght() > 150 || claim.getWidth() > 150) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Não pode ser maior que 150x150")));
			return;
		}
		if (claim.getLenght() < 10 || claim.getWidth() < 10) {
			player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Não pode ser menor que 10x10")));
			return;
		}
		for (com.cien.claims.Claim f:CienClaims.CLAIMS.getClaims()) {
			if (f.collidesWith(claim) || claim.collidesWith(f)) {
				player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getErrorPrefix()+"Seu claim estaria em colisão com outro claim.")));
				return;
			}
		}
		CienClaims.CLAIMS.addClaim(claim);
		claim.makeFences();
		player.addChatMessage(new ChatComponentText(Util.fixColors(Util.getPrefix()+"Claim Criado com Sucesso!")));
	}

}
