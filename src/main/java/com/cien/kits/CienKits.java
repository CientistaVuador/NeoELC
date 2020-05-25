package com.cien.kits;

import java.util.ArrayList;
import java.util.List;
import com.cien.Module;
import com.cien.data.Properties;
import com.cien.kits.commands.KitBuilder;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CienKits extends Module {
	public static final CienKits KITS = new CienKits();
	
	private final List<Kit> kits = new ArrayList<>();
	
	private CienKits() {
		super("CienKits");
	}
	
	@Override
	public void postStart() {
		System.out.println("Carregando kits.");
		for (String name:Properties.getAllProperties()) {
			if (name.startsWith("(Kit)")) {
				try {
					kits.add(new Kit(Properties.getProperties(name)));
				} catch (Exception ex) {
					System.out.println("Erro ao carregar kit -> "+name+": "+ex.getMessage());
				}
			}
		}
		System.out.println(kits.size()+" Kits carregados");
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new KitBuilder());
    	event.registerServerCommand(new com.cien.kits.commands.Kit());
	}
	
	public void addKit(Kit k) {
		kits.add(k);
	}
	
	public Kit[] getKits() {
		return kits.toArray(new Kit[kits.size()]);
	}
	
	public Kit getKit(String name) {
		for (Kit k:getKits()) {
			if (k.getName().equals(name)) {
				return k;
			}
		}
		return null;
	}
	
	public boolean removeKit(Kit k) {
		return kits.remove(k);
	}
	
	public boolean contains(Kit k) {
		return kits.contains(k);
	}
	
	public boolean containsKit(String name) {
		return getKit(name) != null;
	}
}
