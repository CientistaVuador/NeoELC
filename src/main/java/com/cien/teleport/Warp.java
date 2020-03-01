package com.cien.teleport;


public class Warp {
	
	private final String name;
	private final String world;
	private final float x;
	private final float y;
	private final float z;
	private final float pitch;
	private final float yaw;
	
	
	public Warp(String name, String world, float x, float y, float z, float pitch, float yaw) {
		this.name = name;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	public String getName() {
		return name;
	}
	
	public String getWorld() {
		return world;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!obj.getClass().equals(Warp.class)) {
			return false;
		}
		Warp o = (Warp)obj;
		if (o.name.equals(name)) {
			return true;
		}
		return false;
	}
	
}
