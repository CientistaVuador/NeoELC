package com.cien.login.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.login.CienLogin;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class SetPassword extends CienCommandBase {

	public SetPassword() {
		super("setpassword", "Altera sua senha.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length != 2) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /setpassword <Senha> <Senha>");
			return;
		}
		if (!args[0].equals(args[1])) {
			Util.sendMessage(player, Util.getErrorPrefix()+"As Senhas não são iguais.");
			return;
		}
		CienLogin.LOGIN.setPassword(player.getCommandSenderName(), args[0]);
		Util.sendMessage(player, Util.getPrefix()+"Senha alterada!");
	}

}
