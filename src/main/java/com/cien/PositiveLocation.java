package com.cien;

public class PositiveLocation {
	
	private static int bigger(int a, int b) {
		if (a > b) {
			return a;
		}
		return b;
	}
	
	private static int smaller(int a, int b) {
		if (a < b) {
			return a;
		}
		return b;
	}
	
	public static final int MAX = 2999999;
	
	private final int x;
	private final int y;
	private final int z;
	
	public PositiveLocation(int x, int y, int z) {
		this.x = x + MAX;
		this.y = y;
		this.z = z + MAX;
	}
	
	public int getX() {
		return x - MAX;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z - MAX;
	}
	
	public int getRealX() {
		return x;
	}
	
	public int getRealY() {
		return y;
	}
	
	public int getRealZ() {
		return z;
	}
	
	public PositiveLocation add(int x, int y, int z) {
		return new PositiveLocation(getRealX()+x, getRealY()+y, getRealZ()+z);
	}
	
	public PositiveLocation remove(int x, int y, int z) {
		return new PositiveLocation(getRealX()-x, getRealY()-y, getRealZ()-z);
	}
	
	public int distance(PositiveLocation loc) {
		return (int)Math.sqrt(
				Math.pow(bigger(loc.x, x) - smaller(loc.x, x), 2) +
				Math.pow(bigger(loc.y, y) - smaller(loc.y, y), 2) +
				Math.pow(bigger(loc.z, z) - smaller(loc.z, z), 2)
				);
	}
	
	public int distanceXZ(PositiveLocation loc) {
		return (int)Math.sqrt(
				Math.pow(bigger(loc.x, x) - smaller(loc.x, x), 2) +
				Math.pow(bigger(loc.z, z) - smaller(loc.z, z), 2)
				);
	}
}
