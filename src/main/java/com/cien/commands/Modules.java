package com.cien.commands;

import com.cien.CienCommandBase;
import com.cien.Module;
import com.cien.ModuleManager;
import com.cien.Util;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Modules extends CienCommandBase {

	public Modules() {
		super("modules", "Mostra todos os módulos do servidor.");
	}

	@Override
	public void onCommand(ICommandSender sender, EntityPlayerMP player, String[] args) {
		Module[] modules = ModuleManager.getModules();
		Util.sendMessage(sender, Util.getPrefix()+"PT = Pré Tick, T = Tick, PS = Pós Tick, TT = Task Tick");
		Util.sendMessage(sender, Util.getPrefix()+"Modulos: ");
		for (Module m:modules) {
			Util.sendMessage(sender, " §6"+m.getName()+", PT: "+m.getPreTickTime()+", T: "+m.getTickTime()+", PS: "+m.getPostTickTime()+", TT: "+m.getTaskTickTime());
		}
	}

}
