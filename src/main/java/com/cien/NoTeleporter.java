package com.cien;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class NoTeleporter extends Teleporter {

	public NoTeleporter(WorldServer p_i1963_1_) {
		super(p_i1963_1_);
	}

	@Override
	public boolean makePortal(Entity p_85188_1_) {
		return true;
	}
	
	@Override
	public void placeInPortal(Entity p_77185_1_, double p_77185_2_, double p_77185_4_, double p_77185_6_,
			float p_77185_8_) {
		
	}
	
}
