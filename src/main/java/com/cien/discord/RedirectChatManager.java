package com.cien.discord;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cien.Util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class RedirectChatManager {
	
	private static RedirectChatManager theManager = null;
	
	public static RedirectChatManager getInstance() {
		if (theManager == null) {
			theManager = new RedirectChatManager();
		}
		return theManager;
	}
	
	private Queue<Object[]> queue = new ConcurrentLinkedQueue<>();
	
	private User currentUser = null;
	private Message currentMessage = null;
	private String currentMessageText = null;
	private int timeLeft = 0;
	
	private RedirectChatManager() {
		Util.schedule("Redirect Chat Manager Update", () -> {
			theManager.update();
		}, 20);
	}
	
	public void redirect(User s, String msg) {
		Object[] obj = {msg, s};
		queue.add(obj);
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
	
	public Message getCurrentMessage() {
		return currentMessage;
	}
	
	public String getCurrentMessageText() {
		return currentMessageText;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public int getQueueSize() {
		return queue.size();
	}
	
	private void update() {
		TextChannel redirectChannel = CienDiscord.DISCORD.getRedirectChat();
		if (redirectChannel == null) {
			return;
		}
		Role role = CienDiscord.DISCORD.getRedirectRole();
		if (role == null) {
			return;
		}
		if (timeLeft <= 0) {
			if (currentMessage != null) {
				currentMessage.delete().queue();
				currentMessage = null;
				redirectChannel.getGuild().removeRoleFromMember(currentUser.getIdLong(), role).queue();
				currentUser = null;
				currentMessageText = null;
			}
			Object[] a = queue.poll();
			if (a != null) {
				currentMessageText = (String) a[0];
				currentUser = (User) a[1];
				currentMessage = redirectChannel.sendMessage("<@"+currentUser.getIdLong()+"> "+currentMessageText).complete();
				timeLeft = 60;
				redirectChannel.getGuild().addRoleToMember(currentUser.getIdLong(), role).queue();
			}
		} else {
			timeLeft--;
		}
	}
}
