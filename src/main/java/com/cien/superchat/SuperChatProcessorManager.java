package com.cien.superchat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class SuperChatProcessorManager {
	
	public static class ItemProcessor extends SuperChatProcessor {

		public ItemProcessor() {
			super("item");
		}

		@Override
		public IChatComponent process(String[] args, String msg, String unformmated) {
			try {
				if (args.length == 0) {
					return null;
				}
				int id;
				try {
					id = Integer.parseInt(args[0]);
				} catch (NumberFormatException ex) {
					return null;
				}
				int meta = 0;
				if (args.length > 1) {
					try {
						meta = Integer.parseInt(args[1]);
					} catch (NumberFormatException ex) {
						return null;
					}
				}
				
				if (id < 0 || meta < 0) {
					return null;
				}
				
				Item it = Item.getItemById(id);
				
				if (it == null) {
					return null;
				}
				
				ItemStack st = new ItemStack(it, 1, meta);
				
				String name = st.getDisplayName();
				
				if (name == null) {
					return null;
				}
				
				IChatComponent result = new ChatComponentText(name);
				ChatStyle style = new ChatStyle();
				style.setColor(EnumChatFormatting.DARK_AQUA);
				style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§3"+name+" - "+id+":"+meta)));
				style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.google.com/search?q=Minecraft+"+name.replace(' ', '+')));
				result.setChatStyle(style);
				return result;
			} catch (Exception ex) {
				return null;
			}
		}
		
	}
	
	public static class PegarInfinityProcessor extends SuperChatClickProcessor {

		public PegarInfinityProcessor() {
			super("pegarinfinity", "[PegarInfinity]", "§aCLIQUE PARA PEGAR INFINITY INGOT!");
		}

		@Override
		public void onClick(ICommandSender sender) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			player.setFire(240);
			MinecraftServer.getServer().getCommandManager().executeCommand(sender, "/g Peguei infinity ingot clicando aq!!! -> ~pegarinfinity: ");
		}
		
	}
	
	public static class SpoilerProcessor extends SuperChatProcessor {

		public SpoilerProcessor() {
			super("spoiler");
		}

		@Override
		public IChatComponent process(String[] args, String msg, String unformmated) {
			IChatComponent comp = new ChatComponentText("[Spoiler]");
			ChatStyle style = new ChatStyle();
			style.setColor(EnumChatFormatting.GREEN);
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(msg)));
			comp.setChatStyle(style);
			return comp;
		}
		
	}
	
	public static class CommandProcessor extends SuperChatProcessor {
		
		public CommandProcessor() {
			super("command");
		}

		@Override
		public IChatComponent process(String[] args, String msg, String unformmated) {
			IChatComponent comp = new ChatComponentText("[Comando]");
			ChatStyle style = new ChatStyle();
			style.setColor(EnumChatFormatting.GREEN);
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(msg)));
			msg = msg.replace('§', '&');
			style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, msg));
			comp.setChatStyle(style);
			return comp;
		}
		
	}
	
	public static final class LinkProcessor extends SuperChatProcessor {
		
		public LinkProcessor() {
			super("link");
		}

		@Override
		public IChatComponent process(String[] args, String msg, String unformmated) {
			IChatComponent comp = new ChatComponentText("[Link]");
			ChatStyle style = new ChatStyle();
			style.setColor(EnumChatFormatting.GREEN);
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(msg)));
			style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, msg));
			comp.setChatStyle(style);
			return comp;
		}
		
	}
	
	public static class GGProcessor extends SuperChatProcessor {
		
		public GGProcessor() {
			super("gg");
		}

		@Override
		public IChatComponent process(String[] args, String msg, String unformmated) {
			IChatComponent comp = new ChatComponentText("§a[GG!]");
			ChatStyle style = new ChatStyle();
			style.setColor(EnumChatFormatting.GREEN);
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§aGG!")));
			style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g GG!"));
			comp.setChatStyle(style);
			return comp;
		}
		
	}
	
	private static final List<SuperChatProcessor> processors = new ArrayList<>();
	
	static {
		addProcessor(new CommandProcessor());
		addProcessor(new LinkProcessor());
		addProcessor(new SpoilerProcessor());
		addProcessor(new GGProcessor());
		addProcessor(new PegarInfinityProcessor());
		addProcessor(new ItemProcessor());
	}
	
	public static boolean addProcessor(SuperChatProcessor pro) {
		if (!processors.contains(pro)) {
			processors.add(pro);
			return true;
		}
		return false;
	}
	
	public static boolean removeProcessor(SuperChatProcessor pro) {
		return processors.remove(pro);
	}
	
	public static SuperChatProcessor[] getProcessors() {
		return processors.toArray(new SuperChatProcessor[processors.size()]);
	}
	
	public static SuperChatProcessor getProcessor(String name) {
		name = name.toLowerCase();
		for (SuperChatProcessor p:getProcessors()) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	private SuperChatProcessorManager() {
		
	}
}
