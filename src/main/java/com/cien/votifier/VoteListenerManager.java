package com.cien.votifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoteListenerManager {

	private static final List<VoteListener> listeners = new ArrayList<>();
	private static final Queue<Vote> queue = new ConcurrentLinkedQueue<>();
	
	public static VoteListener[] getListeners() {
		return listeners.toArray(new VoteListener[listeners.size()]);
	}
	
	public static void addListener(VoteListener l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}
	
	public static boolean removeListener(VoteListener l) {
		return listeners.remove(l);
	}
	
	public static void addToQueue(Vote v) {
		queue.add(v);
	}
	
	public static void callListeners() {
		Vote v = queue.poll();
		if (v != null) {
			for (VoteListener f:getListeners()) {
				try {
					f.onVote(v);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private VoteListenerManager() {
		
	}

}
