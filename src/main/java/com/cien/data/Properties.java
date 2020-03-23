package com.cien.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

public final class Properties {
	
	private static final List<Properties> created = new ArrayList<>();
	public static final File DATA = new File("data");
	
	public static Properties getProperties(String name) {
		for (Properties p:created) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		Properties p = new Properties(name);
		created.add(p);
		return p;
	}
	
	public static String[] getAllProperties() {
		List<String> names = new ArrayList<>();
		forEach(Properties::save);
		for (File f:DATA.listFiles()) {
			String name = f.getName();
			if (name.endsWith(".data")) {
				names.add(name.substring(0, name.length()-".data".length()));
			}
		}
		return names.toArray(new String[names.size()]);
	}
	
	public static boolean hasProperties(String name) {
		for (Properties p:created) {
			if (p.getName().equals(name)) {
				return true;
			}
		}
		Properties g = new Properties(name);
		return g.exists();
	}
	
	public static void forEach(Consumer<? super Properties> consumer) {
		created.forEach(consumer);
	}
	
	private static String escape(String s) {
		StringBuilder b = new StringBuilder(64);
		for (char c:s.toCharArray()) {
			switch (c) {
				case '=':
				case '\\':
				case '\n':
					b.append('\\');
			}
			b.append(c);
		}
		return b.toString();
	}
	
	private final Map<String, String> map = new HashMap<>();
	private final Map<String, Object> memory = new HashMap<>();
	private final String name;
	private final File file;
	
	private Properties(String name) {
		this.name = name;
		this.file = new File("data", name+".data");
		if (!DATA.exists()) {
			DATA.mkdirs();
		}
		load();
	}
	
	
	public boolean exists() {
		return file.exists();
	}
	
	public String getName() {
		return name;
	}
	
	public File getFile() {
		return file;
	}
	
	public void reload() {
		map.clear();
		load();
	}
	
	public Set<Entry<String, String>> getEntries() {
		return map.entrySet();
	}
	
	public boolean delete() {
		file.delete();
		return created.remove(this);
	}
	
	public void save() {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8"));
			for (Entry<String, String> entry:map.entrySet()) {
				writer.write(escape(entry.getKey()));
				writer.write("=");
				writer.write(escape(entry.getValue()));
				writer.write("\n");
			}
			writer.close();
		} catch (IOException ex) {
			System.out.println("Erro ao salvar '"+name+"': "+ex.getMessage());
		}
	}
	
	public void load() {
		if (!exists()) {
			return;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), "UTF-8"));
			StringBuilder b = new StringBuilder(64);
			String key = null;
			boolean escape = false;
			int f;
			while ((f = reader.read()) != -1) {
				char c = (char) f;
				if (escape) {
					b.append(c);
					escape = false;
					continue;
				}
				if (c == '=') {
					key = b.toString();
					b.setLength(0);
					continue;
				}
				if (c == '\n') {
					map.put(key, b.toString());
					key = null;
					b.setLength(0);
					continue;
				}
				if (c == '\\') {
					escape = true;
					continue;
				}
				b.append(c);
			}
			reader.close();
		} catch (IOException ex) {
			System.out.println("Erro ao carregar '"+name+"': "+ex.getMessage());
		}
	}
	
	public void remove(String key) {
		map.remove(key);
	}
	
	public void setMemory(String key, Object value) {
		memory.put(key, value);
	}
	
	public Object getMemory(String key) {
		return memory.get(key);
	}
	
	public void set(String key, String value) {
		if (value == null) {
			map.remove(key);
		} else {
			map.put(key, value);
		}
	}
	
	public void setBoolean(String key, boolean value) {
		map.put(key, Boolean.toString(value));
	}
	
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(get(key));
	}
	
	public String get(String key) {
		return map.get(key);
	}
}
