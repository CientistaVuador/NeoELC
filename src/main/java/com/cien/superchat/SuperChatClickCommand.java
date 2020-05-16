package com.cien.superchat;

import com.cien.CienCommandBase;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class SuperChatClickCommand extends CienCommandBase {

	public SuperChatClickCommand() {
		super("sp_click_command", "Comando de click do superchat, uso interno.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			return;
		}
		StringBuilder name = new StringBuilder(64);
		for (int i = 0; i < args.length; i++) {
			name.append(args[i]);
			if (i != (args.length-1)) {
				name.append(' ');
			}
		}
		String n = name.toString();
		SuperChatProcessor proc = SuperChatProcessorManager.getProcessor(n);
		if (proc != null) {
			if (proc instanceof SuperChatClickProcessor) {
				SuperChatClickProcessor s = (SuperChatClickProcessor) proc;
				s.onClick(sender);
			}
		}
	}

}
