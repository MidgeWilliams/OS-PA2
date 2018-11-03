package cs131.pa2.CarsTunnels;

import java.util.*;

import cs131.pa2.Abstract.*;
import cs131.pa2.Abstract.Log.*;

public class PriorityScheduler extends Tunnel{
	
	private Collection<Tunnel> tunnels;
	private ArrayList<ArrayList<Vehicle>> priority;
	
	public PriorityScheduler(String name) {
		super(name);
		tunnels = new ArrayList<>();
		makePriorityLists();
	}
	
	public PriorityScheduler(String name, Collection<Tunnel> tunnels, Log log) {
		super(name, log);
		this.tunnels = tunnels;
		makePriorityLists();
	}
	
	private void makePriorityLists() {
		priority = new ArrayList<ArrayList<Vehicle>>();
		for(int i = 0; i < 5; i++) {
			priority.add(new ArrayList<>());
		}
	}
	
	@Override
	public boolean tryToEnterInner(Vehicle vehicle) {
		while(true) {
			int currPr = vehicle.getPriority();
			if(!priority.get(currPr).contains(vehicle)) {
				priority.get(currPr).add(vehicle);
			}
			if(checkHighPrior(currPr) || checkTunnels(vehicle)) {
				
			}
			
			return false;
		}
		
	}
	
	private boolean checkTunnels(Vehicle vehicle) {
		
		for(Tunnel tunnel:tunnels) {
			tunnel.tryToEnter(vehicle);
		}
		return false;
	}
	
	private boolean checkHighPrior(int curr) {
		for(int i = 4; i >= 0; i++) {
			if(!priority.get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void exitTunnelInner(Vehicle vehicle) {
		
	}
	
}
