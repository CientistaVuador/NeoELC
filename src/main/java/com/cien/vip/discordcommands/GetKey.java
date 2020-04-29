package com.cien.vip.discordcommands;

import java.util.List;

import com.cien.data.Properties;
import com.cien.discord.CienDiscord;
import com.cien.discord.DiscordCommand;
import com.cien.vip.CienVIP;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class GetKey extends DiscordCommand {

	public GetKey() {
		super("getkey", "Retorna sua key.");
	}

	@Override
	public void onCommand(User user, Message msg, String[] args, String playerName) {
		Role infinity = CienVIP.VIP.getVipInfinityRole();
		if (infinity == null) {
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Nenhum cargo vip foi definido.");
			return;
		}
		List<Member> member = CienDiscord.DISCORD.getGuild().getMembersWithRoles(infinity);
		boolean found = false;
		for (Member m:member) {
			if (m.getUser().getIdLong() == user.getIdLong()) {
				found = true;
				break;
			}
		}
		if (!found) {
			CienDiscord.DISCORD.sendCommandMessage("<@"+user.getIdLong()+"> Você não é vip eterno.");
			return;
		}
		Properties prop = Properties.getProperties("(DiscordUser)"+user.getIdLong());
		String key = prop.get("vipKey");
		if (key == null) {
			key = CienVIP.VIP.generateKey(0, true).getToken();
			prop.set("vipKey", key);
		}
		CienDiscord.DISCORD.sendPrivateMessage(user, "Sua Key: "+key);
	}

}
