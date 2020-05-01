package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Real extends CienCommandBase {

	public Real() {
		super("real", "Mostra o nome real de um player.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /real <Nome>");
		} else {
			StringBuilder b = new StringBuilder(args.length * 16);
			for (int i = 0; i < args.length; i++) {
				b.append(args[i]);
				if (i != (args.length - 1)) {
					b.append(' ');
				}
			}
			
			
			EntityPlayerMP[] online = Util.getOnlinePlayers();
			String[] cache = new String[online.length];
			String[] nickNotFormmated = new String[online.length];
			int cacheCount = 0;
			int cacheIndex = 0;
			
			String name = removeAllColorsAndToLowerCase(b.toString());
			for (EntityPlayerMP p:online) {
				String e = CienChat.CHAT.getPlayerNick(p.getCommandSenderName());
				cache[cacheCount] = removeAllColorsAndToLowerCase(e);
				nickNotFormmated[cacheCount] = e;
				if (cache[cacheCount].equals(name)) {
					Util.sendMessage(player, Util.getPrefix()+"O Nome real de "+e.replace('&', '§')+"§6 é "+p.getCommandSenderName());
					return;
				}
				cacheCount++;
			}
			
			for (EntityPlayerMP p:online) {
				String e = nickNotFormmated[cacheIndex];
				if (cache[cacheIndex].startsWith(name)) {
					Util.sendMessage(player, Util.getPrefix()+"O Nome real de "+e.replace('&', '§')+"§6 é "+p.getCommandSenderName());
					return;
				}
				cacheIndex++;
			}
			
			cacheIndex = 0;
			for (EntityPlayerMP p:online) {
				String e = nickNotFormmated[cacheIndex];
				if (cache[cacheIndex].contains(name)) {
					Util.sendMessage(player, Util.getPrefix()+"O Nome real de "+e.replace('&', '§')+"§6 é "+p.getCommandSenderName());
					return;
				}
				cacheIndex++;
			}
			
			Util.sendMessage(player, Util.getErrorPrefix()+"Não foi possível encontrar o nome real.");
		}
	}
	
	private String removeAllColorsAndToLowerCase(String s) {
		s = s.toLowerCase();
		StringBuilder b = new StringBuilder(s.length());
		boolean color = false;
		for (char c:s.toCharArray()) {
			if (c == '&' || c == '§') {
				color = true;
				continue;
			}
			if (color) {
				color = false;
				continue;
			}
			b.append(c);
		}
		return b.toString();
	}

}
