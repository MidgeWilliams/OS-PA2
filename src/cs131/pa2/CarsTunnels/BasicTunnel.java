package cs131.pa2.CarsTunnels;

import java.util.LinkedList;

import cs131.pa2.Abstract.*;

public class BasicTunnel extends Tunnel{
	
	private final String CAR = "CAR";
	private final String SLED = "SLED";
	
	private boolean sledWaiting;
	private String currVehicle; //Keeps track of if cars or sleds are inside
	private Direction currDir;  //Keeps track of direction
	
	private LinkedList<Vehicle> inTunnel;
	private LinkedList<Vehicle> waiting;
	
	public BasicTunnel(String name) {
		super(name);

		currDir = null;
		currVehicle = null;
		sledWaiting = false;
		
		inTunnel = new LinkedList<>();
		waiting = new LinkedList<>();
	}

	@Override
	public synchronized boolean tryToEnterInner(Vehicle vehicle) {
		if(currDir == null || vehicle.getDirection().equals(currDir) ) {
			if(vehicle instanceof Sled) {
				
				if(inTunnel.size() == 0) {
					inTunnel.add(vehicle);
					
					if(waiting.contains(vehicle)) {
						waiting.remove(vehicle);
						if(waiting.size() == 0 || !(waiting.getFirst() instanceof Sled)) {
							sledWaiting = false;
						}
					}
					
					currDir = vehicle.getDirection();
					currVehicle = SLED;
					notify();
					return true;
				}else {
					waiting.addFirst(vehicle);
					sledWaiting = true;
				}
				
			}else if(vehicle instanceof Car) {
				if(!sledWaiting && inTunnel.size() == 0){
					inTunnel.add(vehicle);
					carOffWait(vehicle);
					currDir = vehicle.getDirection();
					currVehicle = CAR;
					notify();
					return true;
				}else if(!sledWaiting && currDir.equals(vehicle.getDirection()) 
						&& currVehicle.equals(CAR) && inTunnel.size() < 3) {
					inTunnel.add(vehicle);
					carOffWait(vehicle);
					notify();
					return true;
				}else {
					waiting.addLast(vehicle);
				}
				
			}
		}
		notify();
		return false;
	}

	
	public boolean carOffWait(Vehicle vehicle) {
		if(waiting.contains(vehicle)) {
			waiting.remove(vehicle);
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized void exitTunnelInner(Vehicle vehicle) {
		if(vehicle instanceof Sled && inTunnel.contains(vehicle)) {
			inTunnel.remove(vehicle);
			currDir = null;
			currVehicle = null;
			notify();
		}else if(vehicle instanceof Car && inTunnel.contains(vehicle)) {
			inTunnel.remove(vehicle);
			if(inTunnel.size() == 0) {
				currDir = null;
				currVehicle = null;
			}
			notify();
		}
	}
	
}
