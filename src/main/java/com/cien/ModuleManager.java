package com.cien;

import java.util.ArrayList;
import java.util.List;
import com.cien.Module.ModuleRunnable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;

public class ModuleManager {
	
	private static boolean start = false;
	private static boolean command = false;
	
	private static long totalTickTime = 0;
	
	private static long preTickTime = 0;
	private static long tickTime = 0;
	private static long postTickTime = 0;
	private static long taskTickTime = 0;
	
	private static final List<Module> modules = new ArrayList<>();
	
	public static boolean register(Module m) {
		if (modules.contains(m)) {
			return false;
		}
		modules.add(m);
		FMLCommonHandler.instance().bus().register(m);
        MinecraftForge.EVENT_BUS.register(m);
		return true;
	}
	
	public static boolean unregister(Module m) {
		boolean b = modules.remove(m);;
		if (b) {
			FMLCommonHandler.instance().bus().unregister(m);
	        MinecraftForge.EVENT_BUS.unregister(m);
		}
		return b;
	}
	
	public static Module[] getModules() {
		return modules.toArray(new Module[modules.size()]);
	}
	
	public static Module getModule(String name) {
		for (Module m:getModules()) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	
	public static void registerCommands(FMLServerStartingEvent event) {
		if (command) {
			return;
		}
		command = true;
		Module[] modules = getModules();
		for (Module m:modules) {
			try {
				m.registerCommands(event);
			} catch (Exception ex) {
				System.out.println("Exception in command registry of "+m.getName());
				ex.printStackTrace();
			}
		}
	}
	
	public static void start() {
		if (start) {
			return;
		}
		start = true;
		
		Module[] modules = getModules();
		
		for (Module m:modules) {
			try {
				System.out.println("Pré-Iniciando "+m.getName());
				m.preStart();
			} catch (Exception ex) {
				System.out.println("Exception at Module "+m.getName());
				ex.printStackTrace();
			}
		}
		
		for (Module m:modules) {
			try {
				System.out.println("Iniciando "+m.getName());
				m.start();
			} catch (Exception ex) {
				System.out.println("Exception at Module "+m.getName());
				ex.printStackTrace();
			}
		}
		
		for (Module m:modules) {
			try {
				System.out.println("Pós-Iniciando "+m.getName());
				m.postStart();
			} catch (Exception ex) {
				System.out.println("Exception at Module "+m.getName());
				ex.printStackTrace();
			}
		}
	}
	
	public static void tick() {
		long h = System.nanoTime();
		Module[] modules = getModules();
		
		long t = System.nanoTime();
		for (Module m:modules) {
			try {
				long here = System.nanoTime();
				Object[][] tasks = m.tasks.toArray(new Object[m.tasks.size()][]);
				for (Object[] obj:tasks) {
					try {
						ModuleRunnable runnable = (ModuleRunnable) obj[0];
						int ticks = (int) obj[1];
						boolean schedule = (boolean) obj[2];
						int ticksLeft = (int) obj[3];
						if (runnable.isCancelled()) {
							m.tasks.remove(obj);
							continue;
						}
						
						if (!schedule) {
							if (ticks <= 0) {
								runnable.run(m, runnable);
								runnable.cancel();
							} else {
								m.run(runnable, ticks-1);
							}
						} else {
							if (ticksLeft <= 0) {
								runnable.run(m, runnable);
								Object[] ar = {runnable, ticks, schedule, ticks};
								m.tasks.add(ar);
							} else {
								Object[] ar = {runnable, ticks, schedule, ticksLeft-1};
								m.tasks.add(ar);
							}
						}
						m.tasks.remove(obj);
					} catch (Exception ex) {
						System.out.println("Exception at task in Module "+m.getName());
						ex.printStackTrace();
					}
				}
				m.taskTickTime = System.nanoTime() - here;
			} catch (Exception ex) {
				System.out.println("Exception at Module "+m.getName());
				ex.printStackTrace();
			}
		}
		taskTickTime = System.nanoTime() - t;
		
		t = System.nanoTime();
		for (Module m:modules) {
			try {
				long here = System.nanoTime();
				m.preTick();
				m.preTickTime = System.nanoTime() - here;
			} catch (Exception ex) {
				System.out.println("Exception at Module "+m.getName());
				ex.printStackTrace();
			}
		}
		preTickTime = System.nanoTime() - t;
		
		t = System.nanoTime();
		for (Module m:modules) {
			try {
				long here = System.nanoTime();
				m.tick();
				m.tickTime = System.nanoTime() - here;
			} catch (Exception ex) {
				System.out.println("Exception at Module "+m.getName());
				ex.printStackTrace();
			}
		}
		tickTime = System.nanoTime() - t;
		
		t = System.nanoTime();
		for (Module m:modules) {
			try {
				long here = System.nanoTime();
				m.postTick();
				m.postTickTime = System.nanoTime() - here;
			} catch (Exception ex) {
				System.out.println("Exception at Module "+m.getName());
				ex.printStackTrace();
			}
		}
		postTickTime = System.nanoTime() - t;
		
		totalTickTime = System.nanoTime() - h;
	}
	
	public static long getTotalTickTime() {
		return totalTickTime;
	}
	
	public static long getPostTickTime() {
		return postTickTime;
	}
	
	public static long getPreTickTime() {
		return preTickTime;
	}
	
	public static long getTaskTickTime() {
		return taskTickTime;
	}
	
	public static long getTickTime() {
		return tickTime;
	}
	
	private ModuleManager() {
		
	}
	
}
