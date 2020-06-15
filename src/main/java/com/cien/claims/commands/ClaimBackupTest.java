package com.cien.claims.commands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class ClaimBackupTest extends CienCommandBase {

	public ClaimBackupTest() {
		super("claim_backup_test", "Teste de backup do claim");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.claim_backup_test")) {
			Util.sendMessage(player, "Sem Permissão.");
			return;
		}
		com.cien.claims.Claim current = CienClaims.CLAIMS.getClaimInside(player);
		if (current == null) {
			Util.sendMessage(player, "Fique sobre um claim");
			return;
		}
		File write = new File("teste.claim");
		Util.sendMessage(player, "Escrevendo em 'teste.claim'...");
		try {
			long here = System.currentTimeMillis();
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(write));
			byte[] bytes = current.getBlocksAsBytes();
			out.write(bytes);
			out.close();
			long result = System.currentTimeMillis() - here;
			Util.sendMessage(player, "Feito! em "+result+" ms");
		} catch (Exception ex) {
			Util.sendMessage(player, "Erro: "+ex.getMessage());
			ex.printStackTrace();
		}
	}

}
