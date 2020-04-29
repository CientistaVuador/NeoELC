package com.cien.vip;

public final class Key {
	
	private final String token;
	private final long time;
	
	protected Key(String token, long time) {
		this.token = token;
		this.time = time;
	}
	
	public long getTime() {
		return time;
	}
	
	public String getToken() {
		return token;
	}
	
	public boolean isInfinity() {
		return time < 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != Key.class) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		Key k = (Key) obj;
		if (!k.token.equals(this.token)) {
			return false;
		}
		return true;
	}
	
	protected static String toString(Key k) {
		return k.token+":"+k.time;
	}
	
	protected static Key fromString(String s) {
		String[] split = s.split(":");
		return new Key(split[0], Long.parseLong(split[1]));
	}
}
