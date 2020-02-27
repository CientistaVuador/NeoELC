package com.cien;

public class ScheduledTask {
	
	private final Runnable r;
	private final int ticks;
	private int tick = 0;
	private boolean complete = false;
	private final String name;
	
	public ScheduledTask(String name, Runnable r, int ticks) {
		this.r = r;
		this.ticks = ticks;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTicks() {
		return ticks;
	}
	
	public void tick() {
		if (complete) {
			return;
		}
		if (tick >= ticks) {
			try {
				r.run();
			} catch (Exception ex) {
				System.out.println("Erro na scheduled task '"+name+"': "+ex.getMessage());
			}
			tick = 0;
		}
		tick++;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
}
