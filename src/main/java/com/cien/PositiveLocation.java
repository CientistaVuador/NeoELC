package com.cien;

public class PositiveLocation {
	
	public static int pos(int a) {
		if (a < 0) {
            return a * (-1);
        }
        return a;
	}
	
	public static int bigger(int a, int b) {
		if (a > b) {
			return a;
		}
		return b;
	}
	
	public static int smaller(int a, int b) {
		if (a < b) {
			return a;
		}
		return b;
	}
	
	public static PositiveLocation biggerX(PositiveLocation a, PositiveLocation b) {
		if (a.x > b.x) {
			return a;
		}
		return b;
	}
	
	public static PositiveLocation smallerX(PositiveLocation a, PositiveLocation b) {
		if (a.x > b.x) {
			return b;
		}
		return a;
	}
	
	public static boolean insideXZ(PositiveLocation a, PositiveLocation b, PositiveLocation point) {
		int bigX = bigger(a.x, b.x);
		int smlX = smaller(a.x, b.x);
		
		int bigZ = bigger(a.z, b.z);
		int smlZ = smaller(a.z, b.z);
		
		if (point.x > bigX) {
			return false;
		}
		if (point.x < smlX) {
			return false;
		}
		if (point.z > bigZ) {
			return false;
		}
		if (point.z < smlZ) {
			return false;
		}
		return true;
	}
	
	public static final int MAX = 3000000;
	
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
	
	public int getPositiveX() {
		return x;
	}
	
	public int getPositiveY() {
		return y;
	}
	
	public int getPositiveZ() {
		return z;
	}
	
	public PositiveLocation add(int x, int y, int z) {
		return new PositiveLocation(this.x+x, this.y+y, this.z+z);
	}
	
	public PositiveLocation remove(int x, int y, int z) {
		return new PositiveLocation(this.x-x, this.y-y, this.z-z);
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
	
	public int distanceX(PositiveLocation loc) {
		return bigger(loc.x, x) - smaller(loc.x, x);
	}
	
	public int distanceZ(PositiveLocation loc) {
		return bigger(loc.z, z) - smaller(loc.z, z); 
	}
	
	public int distanceY(PositiveLocation loc) {
		return bigger(loc.y, y) - smaller(loc.y, y);
	}
}
