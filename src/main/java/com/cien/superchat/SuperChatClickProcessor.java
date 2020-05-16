package com.cien.superchat;

import java.util.function.Consumer;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public abstract class SuperChatClickProcessor extends SuperChatProcessor {
	
	private static long id = 0;
	
	private static String newID() {
		int part1 = (int) (Math.random() * Short.MAX_VALUE);
		id++;
		return Integer.toString(part1)+"-"+id;
	}
	
	public static SuperChatClickProcessor createSingleUseProcessor(String text, String description, Consumer<ICommandSender> consumer) {
		SuperChatClickProcessor f = new SuperChatClickProcessor(newID(), text, description) {
			@Override
			public void onClick(ICommandSender sender) {
				consumer.accept(sender);
				SuperChatProcessorManager.removeProcessor(this);
			}
		};
		SuperChatProcessorManager.addProcessor(f);
		return f;
	}

	private final String text;
	private final String description;
	
	public SuperChatClickProcessor(String name, String text, String description) {
		super(name);
		this.text = text;
		this.description = description;
	}
	
	public String getText() {
		return text;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public IChatComponent process(String[] args, String msg, String unformmated) {
		IChatComponent comp = new ChatComponentText(text);
		ChatStyle style = new ChatStyle();
		style.setColor(EnumChatFormatting.GREEN);
		style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(description)));
		style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sp_click_command "+getName()));
		comp.setChatStyle(style);
		return comp;
	}
	
	
	public abstract void onClick(ICommandSender sender);
	
}
