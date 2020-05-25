package com.cien.chat.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.chat.CienChat;
import com.cien.superchat.SuperChatProcessor;
import com.cien.superchat.SuperChatProcessorManager;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class Ignorar extends CienCommandBase {

	public static class IgnoredMessageSuperChatCommand extends SuperChatProcessor {

		static {
			SuperChatProcessorManager.addProcessor(new IgnoredMessageSuperChatCommand());
		}
		
		public IgnoredMessageSuperChatCommand() {
			super("ignorada");
		}

		@Override
		public IChatComponent process(String[] args, String msg, String unformmated) {
			IChatComponent comp = new ChatComponentText("[Mensagem Ignorada]");
			ChatStyle style = new ChatStyle();
			style.setColor(EnumChatFormatting.RED);
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(msg)));
			comp.setChatStyle(style);
			return comp;
		}
		
		public static String buildCommandForMessage(String msg) {
			StringBuilder b = new StringBuilder(msg.length()*2);
			b.append("~ignorada:");
			for (char c:msg.toCharArray()) {
				if (c == '\\') {
					b.append('\\');
				}
				if (c == ',') {
					b.append('\\');
				}
				if (c == ' ') {
					b.append(',');
					continue;
				}
				b.append(c);
			}
			return b.toString();
		}
		
	}
	
	public Ignorar() {
		super("ignorar", "Ignora um player");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (args.length != 1) {
			Util.sendMessage(sender, Util.getErrorPrefix()+"Uso: /ignorar <Player>");
			return;
		}
		EntityPlayerMP pl = Util.getOnlinePlayerInexact(args[0]);
		if (pl == null) {
			Util.sendMessage(sender, Util.getErrorPrefix()+"Não há ninguém chamado '"+args[0]+"' online");
			return;
		}
		if (CienChat.CHAT.setIgnoringPlayer(sender.getCommandSenderName(), pl.getCommandSenderName())) {
			Util.sendMessage(sender, Util.getPrefix()+pl.getCommandSenderName()+" Está sendo ignorado.");
		} else {
			Util.sendMessage(sender, Util.getPrefix()+pl.getCommandSenderName()+" Não está mais sendo ignorado.");
		}
	}

}
