package com.cien.announcer.commands;

import com.cien.CienCommandBase;
import com.cien.Util;
import com.cien.announcer.CienAnnouncer;
import com.cien.permissions.CienPermissions;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Announcer extends CienCommandBase {

	public Announcer() {
		super("announcer", "Comando de anúncios");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		if (!CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.announcer")) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão.");
			return;
		}
		if (args.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /announcer <add/rem/list/settime>");
			return;
		}
		switch (args[0].toLowerCase()) {
		case "add":
			onAdd(player, args);
			return;
		case "rem":
			onRem(player, args);
			return;
		case "list":
			onList(player, args);
			return;
		case "settime":
			onSetTime(player, args);
			return;
		}
		Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /announcer <add/rem/list/settime>");
	}
	
	private void onAdd(EntityPlayerMP player, String[] args) {
		if (args.length == 1) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /announcer add <Anuncio>");
			Util.sendMessage(player, Util.getErrorPrefix()+"\\n -> Pula linha");
			Util.sendMessage(player, Util.getErrorPrefix()+"\\t -> Tab");
			Util.sendMessage(player, Util.getErrorPrefix()+"\\p -> Prefixo padrão");
			return;
		}
		StringBuilder builder = new StringBuilder(64);
		for (int i = 1; i < args.length; i++) {
			builder.append(args[i]);
			if (i != (args.length-1)) {
				builder.append(' ');
			}
		}
		String an = builder.toString();
		boolean b = CienAnnouncer.ANNOUNCER.addAnnouncement(an);
		if (b) {
			Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
		} else {
			Util.sendMessage(player, Util.getErrorPrefix()+"Esse anúncio já foi adicionado.");
		}
	}
	
	private void onRem(EntityPlayerMP player, String[] args) {
		if (args.length == 1) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /announcer rem <ID>");
			return;
		}
		int id;
		try {
			id = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
			return;
		}
		if (id > CienAnnouncer.ANNOUNCER.getSize()-1 || id < 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"ID Inválido.");
			return;
		}
		CienAnnouncer.ANNOUNCER.removeAnnouncement(id);
		Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
	}
	
	private void onList(EntityPlayerMP player, String[] args) {
		String[] ann = CienAnnouncer.ANNOUNCER.getAnnouncements();
		if (ann.length == 0) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Nenhum anúncio foi criado.");
			return;
		}
		int index = 0;
		Util.sendMessage(player, Util.getPrefix()+"Anúncios:");
		for (String s:ann) {
			Util.sendMessage(player, " §6"+index+" - '"+s+"'");
			index++;
		}
	}
	
	private void onSetTime(EntityPlayerMP player, String[] args) {
		if (args.length == 1) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Uso: /announcer settime <Tempo em Segs>");
			Util.sendMessage(player, Util.getErrorPrefix()+"Atual: "+CienAnnouncer.ANNOUNCER.getAnnouncerTime()+" seg(s)");
			return;
		}
		int time;
		try {
			time = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			Util.sendMessage(player, Util.getErrorPrefix()+"Erro: "+ex.getMessage());
			return;
		}
		CienAnnouncer.ANNOUNCER.setAnnouncerTime(time);
		Util.sendMessage(player, Util.getPrefix()+"Sucesso!");
	}

}
