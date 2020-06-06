package com.cien.data;

import java.util.ArrayList;
import java.util.List;

public class StringArray {

	private StringArray() {
		
	}
	
	private static String escape(String s) {
		StringBuilder b = new StringBuilder(64);
		for (char c:s.toCharArray()) {
			switch (c) {
				case '\\':
				case '"':
					b.append('\\');
			}
			b.append(c);
		}
		return b.toString();
	}
	
	public static String fromStringArray(String... strs) {
		StringBuilder b = new StringBuilder(64*strs.length);
		for (int i = 0; i < strs.length; i++) {
			b.append('"');
			b.append(escape(strs[i]));
			if (i != (strs.length - 1)) {
				b.append("\", ");
			} else {
				b.append('"');
			}
		}
		return b.toString();
	}
	
	public static String[] toStringArray(String s) {
		if (s == null) {
			return new String[0];
		}
		StringBuilder b = new StringBuilder(s.length());
		boolean escape = false;
		boolean quote = false;
		boolean comma = false;
		List<String> strings = new ArrayList<>();
		FOR_CHAR:
		for (char c:s.toCharArray()) {
			if (escape) {
				b.append(c);
				escape = false;
				continue;
			}
			if (comma) {
				if (c != ',') {
					throw new RuntimeException("Expected ','");
				}
				comma = false;
				strings.add(b.toString());
				b.setLength(0);
				continue;
			}
			if (c == '\\') {
				escape = true;
				continue;
			}
			if (c == '"') {
				quote = !quote;
				if (!quote) {
					comma = true;
				}
				continue;
			}
			if (quote) {
				b.append(c);
				continue;
			}
			switch (c) {
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					continue FOR_CHAR;
			}
			throw new RuntimeException("Invalid char '"+c+"'");
		}
		if (b.length() > 0) {
			strings.add(b.toString());
		}
		return strings.toArray(new String[strings.size()]);
	}

}
