package com.cien;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.MinecraftForge;

public class CienCommandManager extends ServerCommandManager {

	public CienCommandManager() {
		super();
	}

	
	
	@Override
	public int executeCommand(ICommandSender p_71556_1_, String p_71556_2_) {
		CommandPreprocessEvent event = new CommandPreprocessEvent(p_71556_1_, p_71556_2_);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return 1;
		}
		return super.executeCommand(p_71556_1_, p_71556_2_);
	}
	
}
