package cs131.pa2.CarsTunnels;

import java.util.LinkedList;

import cs131.pa2.Abstract.*;

/**
 * 
 * @author Marguerite Williams
 *
 * Just FYI: I have it prioritize the sleds since they need it alone and assume it is less common;
 * Decided to do this based on reader-writer dilemma of starving writers 
 * 
 * Lemme know if anything I did confused you
 */


public class BasicTunnel extends Tunnel{
	
	private final String CAR = "CAR";
	private final String SLED = "SLED";
	
	private boolean sledWaiting; //Boolean to check if there's a sled in the queue
	private String currVehicle; //Keeps track of if cars or sleds are inside
	private Direction currDir;  //Keeps track of direction
	
	private LinkedList<Vehicle> inTunnel; //Vehicles in the tunnel currently
	private LinkedList<Vehicle> waiting; //Vehicles waiting to get into the tunnel
	
	/**
	 * Basic initializing of variables
	 */
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
		if(currDir == null || vehicle.getDirection().equals(currDir) ) { //Makes sure the vehicle is traveling in the right direction
			//SLED
			if(vehicle instanceof Sled) {
				
				if(inTunnel.size() == 0) { //Tunnel has to be empty for sled to enter
					inTunnel.add(vehicle);
					
					offWait(vehicle);
					
					currDir = vehicle.getDirection();
					currVehicle = SLED;
					notify(); //Not sure this needs to be here but feels right, same goes for the rest of the notifies
					return true;
					
				//IF it didn't get in
				}else {
					waiting.addFirst(vehicle); //Adds first for checks later
					sledWaiting = true;
				}
			//CAR
			}else if(vehicle instanceof Car) {
				
				//TUNNEL IS EMPTY AND NO SLEDS WAITING
				if(!sledWaiting && inTunnel.size() == 0){
					inTunnel.add(vehicle);
					offWait(vehicle);
					currDir = vehicle.getDirection();
					currVehicle = CAR;
					notify();
					return true;
					
				//TUNNEL HAS LESS THAN 3 CARS IN IT AND NO SLEDS WAITING
				}else if(!sledWaiting && currVehicle.equals(CAR) && inTunnel.size() < 3) {
					inTunnel.add(vehicle);
					offWait(vehicle);
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

	/**
	 * Takes the given vehicle off of the wait list if it was on it 
	 * @param vehicle
	 * @return True if it was taken off, False if it wasn't (if it got in without waiting)
	 */
	public boolean offWait(Vehicle vehicle) {
		if(waiting.contains(vehicle)) {
			waiting.remove(vehicle);
			if(vehicle instanceof Sled) {
				if(waiting.size() == 0 || !(waiting.getFirst() instanceof Sled)) { 
					/*Checks to see if there is a sled waiting behind the one that just got in 
					 *(always puts sleds at front of list so it will be easy to see if it's there)
					*/
					sledWaiting = false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized void exitTunnelInner(Vehicle vehicle) {
		//Always checks to make sure the vehicle was in the tunnel before trying to remove it just to be safe
		if(vehicle instanceof Sled && inTunnel.contains(vehicle)) {
			
			currDir = null; //Can set to null since the sled had to be the only one in the tunnel; removes it last in case of context switching
			currVehicle = null;
			inTunnel.remove(vehicle);
			notify();
			
		}else if(vehicle instanceof Car && inTunnel.contains(vehicle)) {
			if(inTunnel.size() == 1) {//Only sets to null if it is the only car in the tunnel
				currDir = null;
				currVehicle = null;
			}
			inTunnel.remove(vehicle);
			notify();
		}
	}
	
}
