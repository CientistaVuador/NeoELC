package com.cien.discord;

import cpw.mods.fml.common.eventhandler.Event;
import net.dv8tion.jda.api.events.GenericEvent;

public class DiscordGenericEvent extends Event {

	private final GenericEvent event;
	
	public DiscordGenericEvent(GenericEvent event) {
		this.event = event;
	}
	
	public GenericEvent getEvent() {
		return event;
	}
	
}
