package com.cien.login.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.login.CienLogin;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Login extends CienCommandBase {

	public Login() {
		super("login", "Efetua login no servidor");
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienLogin.LOGIN.getPassword(player.getCommandSenderName()) != null) {
			if (args.length < 1) {
				sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /login <Senha>"));
			} else {
				Properties prop = Properties.getProperties(sender.getCommandSenderName());
				String pas1 = args[0];
				String pas2 = prop.get("password");
				if (pas1.equals(pas2)) {
					boolean b = CienLogin.NEED_LOGIN.remove((EntityPlayerMP)sender);
					if (b) {
						prop.set("lastLoginIP", player.getPlayerIP());
						sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Sucesso!"));
					} else {
						sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Você já está logado."));
					}
				} else {
					sender.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Senha incorreta."));
				}
			}
		} else {
			sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Você não está registrado"));
		}
	}

}
