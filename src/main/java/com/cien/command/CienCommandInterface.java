package com.cien.command;

public interface CienCommandInterface {
	public String getName();
	public CienCommandInterface getParent();
	public String getUsage();
}
