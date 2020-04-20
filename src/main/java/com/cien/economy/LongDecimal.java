package com.cien.economy;

public final class LongDecimal extends Number {
	
	private static final long serialVersionUID = 1L;
	
	private final long value;
	private LongDecimal(long v) {
		this.value = v;
	}
	
	public boolean isNegative() {
		return value < 0;
	}
	
	public boolean isPositive() {
		return value >= 0;
	}
	
	public LongDecimal sum(LongDecimal l) {
		return new LongDecimal(this.value + l.value);
	}
	
	public LongDecimal minus(LongDecimal l) {
		return new LongDecimal(this.value - l.value);
	}
	
	public LongDecimal multiplyBy(LongDecimal l) {
		return new LongDecimal((long)((this.doubleValue() * l.doubleValue()) * 100));
	}
	
	public LongDecimal divideBy(LongDecimal l) {
		return new LongDecimal((long)((this.doubleValue() / l.doubleValue()) * 100));
	}
	
	@Override
	public double doubleValue() {
		return value/100d;
	}

	@Override
	public float floatValue() {
		return value/100f;
	}

	@Override
	public int intValue() {
		return (int) value/100;
	}

	@Override
	public long longValue() {
		return (long) value/100;
	}
	
	public boolean isBiggerThan(LongDecimal dec) {
		if (this.value > dec.value) {
			return true;
		}
		return false;
	}
	
	public boolean isSmallerThan(LongDecimal dec) {
		if (this.value < dec.value) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (LongDecimal.class != obj.getClass()) {
			return false;
		}
		LongDecimal m = (LongDecimal) obj;
		
		return m.value == this.value;
	}
	
	@Override
	public String toString() {
		if (value >= 100 || value <= -100) {
			String p = Long.toString(value);
			char[] array = p.toCharArray();
			char[] toReturn = new char[array.length+1];
			int index = 0;
			for (int i = 0; i < toReturn.length; i++) {
				if (i == (toReturn.length-3)) {
					toReturn[i] = ',';
					continue;
				}
				toReturn[i] = array[index];
				index++;
			}
			return new String(toReturn);
		}
		StringBuilder builder = new StringBuilder(64);
		long pos = value;
		if (isNegative()) {
			pos = pos * -1;
			builder.append('-');
		}
		if (pos >= 10) {
			builder.append("0,");
			builder.append(pos);
		} else {
			builder.append("0,0");
			builder.append(pos);
		}
		return builder.toString();
	}
	
	public static LongDecimal valueOf(int b) {
		return new LongDecimal(b * 100);
	}
	
	public static LongDecimal valueOf(long g) {
		return new LongDecimal(g * 100);
	}
	
	public static LongDecimal valueOf(float f) {
		return new LongDecimal((long)(f * 100));
	}
	
	public static LongDecimal valueOf(double d) {
		return new LongDecimal((long)(d * 100));
	}
	
	public static LongDecimal parse(String text) {
		long value = 0;
		boolean negative = false;
		char[] array = text.toCharArray();
		int numbers = 0;
		boolean decimal = false;
		boolean decimal2 = false;
		int decimalCount = 0;
		int decimalCount2 = 0;
		for (char c:array) {
			if (c >= '0' && c <= '9') {
				if (decimal2) {
					if (decimalCount2 == 2) {
						break;
					}
					decimalCount2++;
				}
				numbers++;
			}
			if (c == ',' || c == '.') {
				decimal2 = true;
			}
		}
		for (char c:array) {
			if (decimal) {
				decimalCount++;
			}
			if (decimalCount > 2) {
				break;
			}
			if (c == '-') {
				negative = true;
				continue;
			}
			if (c == '+') {
				negative = false;
				continue;
			}
			if (c == ',' || c == '.') {
				decimal = true;
				continue;
			}
			if (c >= '0' && c <= '9') {
				value += (Math.pow(10, ((numbers-1) + (2 - decimalCount2))) * valueOf(c));
				numbers--;
			}
		}
		if (negative) {
			value *= -1;
		}
		return new LongDecimal(value);
	}
	
	private static long valueOf(char c) {
		switch (c) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		}
		return 0;
	}
	
	

	
}
