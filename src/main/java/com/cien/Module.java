package com.cien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class Module {
	
	public abstract static class ModuleRunnable {
		
		private boolean cancelled = false;
		public ModuleRunnable() {
			
		}
		
		public abstract void run(Module mdl, ModuleRunnable r);
		
		public boolean isCancelled() {
			return cancelled;
		}
		
		public void cancel() {
			if (cancelled) {
				return;
			}
			cancelled = true;
		}
	}
	
	protected long tickTime = 0;
	protected long preTickTime = 0;
	protected long postTickTime = 0;
	protected long taskTickTime = 0;
	protected final List<Object[]> tasks = Collections.synchronizedList(new ArrayList<>());
	
	private final String name;
	public Module(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public long getTickTime() {
		return tickTime;
	}
	
	public long getPreTickTime() {
		return preTickTime;
	}
	
	public long getPostTickTime() {
		return postTickTime;
	}
	
	public long getTaskTickTime() {
		return taskTickTime;
	}
	
	public ModuleRunnable run(Runnable runnable) {
		return run(runnable, 0);
	}
	
	public ModuleRunnable run(Runnable runnable, int ticks) {
		return run(runnable, ticks, false);
	}
	
	public ModuleRunnable run(Runnable runnable, int ticks, boolean schedule) {
		return run(new ModuleRunnable() {
			@Override
			public void run(Module mdl, ModuleRunnable r) {
				runnable.run();
			}
		}, ticks, schedule);
	}
	
	public ModuleRunnable run(ModuleRunnable r, int ticks, boolean schedule) {
		Object[] arr = {r, ticks, schedule, ticks};
		tasks.add(arr);
		return r;
	}
	
	public ModuleRunnable run(ModuleRunnable r, int ticks) {
		return run(r, ticks, false);
	}
	
	public ModuleRunnable run(ModuleRunnable r) {
		return run(r, 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Module) {
			Module mdl = (Module) obj;
			if (mdl == this) {
				return true;
			}
			if (this.name.equals(mdl.name)) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public void preStart() {};
	public void start() {};
	public void postStart() {};
	
	public void preTick() {};
	public void tick() {};
	public void postTick() {};
	
	public void registerCommands(FMLServerStartingEvent event) {};
}
