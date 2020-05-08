package com.cien.command;

import net.minecraft.item.Item;

public enum ArgumentType {
	TYPE_TEXT("texto"),
	TYPE_INTEGER("inteiro"),
	TYPE_SUBCOMMAND("subcomando"),
	TYPE_TIME_IN_MS("tempo em ms"),
	TYPE_TIME_IN_SECS("tempo em segs"),
	TYPE_NUMERIC_ID("id numérico")
	;
	
	private final String name;
	
	ArgumentType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Object parse(String t, ArgumentType type) {
		if (type == TYPE_TEXT) {
			return t;
		}
		if (type == TYPE_INTEGER) {
			try {
				return Integer.parseInt(t);
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		if (type == TYPE_SUBCOMMAND) {
			return null;
		}
		if (type == TYPE_TIME_IN_MS) {
			try {
				return Long.parseLong(t);
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		if (type == TYPE_TIME_IN_SECS) {
			try {
				return Long.parseLong(t) * 1000;
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		if (type == TYPE_NUMERIC_ID) {
			int[] id = new int[2];
			StringBuilder b = new StringBuilder(64);
			boolean f = false;
			boolean d = false;
			boolean star = false;
			for (char c:t.toCharArray()) {
				if (d) {
					if (c == '*') {
						id[1] = -1;
						d = false;
						star = true;
						break;
					}
				}
				if (c == ':') {
					if (f) {
						return null;
					}
					try {
						id[0] = Integer.parseInt(b.toString());
						if (id[0] < 0) {
							return null;
						}
						Item q = Item.getItemById(id[0]);
						if (q == null) {
							return null;
						}
						b.setLength(0);
						f = true;
						d = true;
					} catch (NumberFormatException ex) {
						return null;
					}
					continue;
				}
				b.append(c);
			}
			if (f && !star) {
				try {
					id[1] = Integer.parseInt(b.toString());
					if (id[1] < 0) {
						return null;
					}
				} catch (NumberFormatException ex) {
					return null;
				}
			} else if (!star) {
				try {
					id[0] = Integer.parseInt(b.toString());
					if (id[0] < 0) {
						return null;
					}
					Item q = Item.getItemById(id[0]);
					if (q == null) {
						return null;
					}
					id[1] = 0;
				} catch (NumberFormatException ex) {
					return null;
				}
			}
		}
		return null;
	}
}
