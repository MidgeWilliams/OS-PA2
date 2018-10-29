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
	
	private String currVehicle; //Keeps track of if cars or sleds are inside
	private Direction currDir;  //Keeps track of direction
	
	private LinkedList<Vehicle> inTunnel; //Vehicles in the tunnel currently
	
	/**
	 * Basic initializing of variables
	 */
	public BasicTunnel(String name) {
		super(name);

		currDir = null;
		currVehicle = null;
		
		inTunnel = new LinkedList<>();
	}

	@Override
	public synchronized boolean tryToEnterInner(Vehicle vehicle) {
		if(currDir == null || vehicle.getDirection().equals(currDir) ) { //Makes sure the vehicle is traveling in the right direction
			//SLED
			if(vehicle instanceof Sled) {
				
				if(inTunnel.size() == 0) { //Tunnel has to be empty for sled to enter
					inTunnel.add(vehicle);		
					currDir = vehicle.getDirection();
					currVehicle = SLED;
					notify(); //Not sure this needs to be here but feels right, same goes for the rest of the notifies
					return true;
					
				}
				
			//CAR
			}else if(vehicle instanceof Car) {
				
				//TUNNEL IS EMPTY 
				if(inTunnel.size() == 0){
					inTunnel.add(vehicle);
					currDir = vehicle.getDirection();
					currVehicle = CAR;
					notify();
					return true;
					
				//TUNNEL HAS LESS THAN 3 CARS IN IT 
				}else if(currVehicle.equals(CAR) && inTunnel.size() < 3) {
					inTunnel.add(vehicle);
					notify();
					return true;
				}
				
			}
		}
		notify();
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
