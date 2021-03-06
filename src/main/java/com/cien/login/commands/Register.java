package com.cien.login.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.login.CienLogin;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Register extends CienCommandBase {

	public Register() {
		super("register", "Efetua registro no servidor");
	}
	
	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (CienLogin.LOGIN.getPassword(player.getCommandSenderName()) == null) {
			if (args.length < 2) {
				sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Uso: /register <Senha> <Senha>"));
			} else {
				String pas1 = args[0];
				String pas2 = args[1];
				if (pas1.equals(pas2)) {
					Properties prop = Properties.getProperties(sender.getCommandSenderName());
					prop.set("password", pas1);
					prop.set("firstLogin", Long.toString(System.currentTimeMillis()));
					prop.set("realName", sender.getCommandSenderName());
					prop.set("lastLoginIP", player.getPlayerIP());
					CienLogin.NEED_REGISTER.remove((EntityPlayerMP)sender);
					sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Sucesso!"));
					prop.save();
				} else {
					sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Senhas incorretas."));
				}
			}
		} else {
			sender.addChatMessage(Util.fixColors(Util.getPrefix()+"Você já está registrado."));
		}
	}
}
