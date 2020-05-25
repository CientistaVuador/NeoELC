package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class Responder extends CienCommandBase {

	public Responder() {
		super("r", "Responde ao último que te mandou uma mensagem privada");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		String last = CienChat.CHAT.getLastSenderFor(player.getCommandSenderName());
		if (last == null) {
			player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Ninguém te mandou uma mensagem privada recentemente."));
		} else {
			if (args.length == 0) {
				player.addChatMessage(Util.fixColors(Util.getErrorPrefix()+"Uso: /r <Mensagem>"));
				return;
			}
			StringBuilder b = new StringBuilder(64);
			for (int i = 0; i < args.length; i++) {
				b.append(args[i]);
				if (i != (args.length - 1)) {
					b.append(' ');
				}
			}
			CienChat.CHAT.run(() -> {
				MinecraftServer.getServer().getCommandManager().executeCommand(sender, "p "+last+" "+b.toString());
			});
		}
	}

}
