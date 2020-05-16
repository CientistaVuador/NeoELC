package com.cien.superchat;

import java.util.Objects;

import net.minecraft.util.IChatComponent;

public abstract class SuperChatProcessor {
	
	private final String name;
	public SuperChatProcessor(String name) {
		this.name = name.toLowerCase();
	}
	
	public String getName() {
		return name;
	}
	
	public String getSuperChatCommandText() {
		return "~"+getName()+":";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SuperChatProcessor) {
			SuperChatProcessor s = (SuperChatProcessor) obj;
			if (s.name.equals(this.name)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 12;
		hash += Objects.hashCode(this.name);
		return hash;
	}
	
	public abstract IChatComponent process(String[] args, String msg, String unformmated);
}
