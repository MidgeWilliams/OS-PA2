package cs131.pa2.CarsTunnels;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import cs131.pa2.Abstract.*;
import cs131.pa2.Abstract.Log.*;

public class PriorityScheduler extends Tunnel {
	private final Lock lock = new ReentrantLock();
	private final Condition exitedFromTunnel = lock.newCondition();
	private Collection<Tunnel> tunnels;
	private ArrayList<ArrayList<Vehicle>> priority;
	private HashMap<Vehicle, Tunnel> vehicleMap;

	/**
	 * @param name
	 */
	public PriorityScheduler(String name) {
		super(name);
		tunnels = new ArrayList<>();
		vehicleMap = new HashMap<>();
		makePriorityLists();
	}

	/**
	 * @param name
	 * @param tunnels
	 * @param log
	 */
	public PriorityScheduler(String name, Collection<Tunnel> tunnels, Log log) {
		super(name, log);
		this.tunnels = tunnels;
		vehicleMap = new HashMap<>();
		makePriorityLists();// set up priority list
	}

	private void makePriorityLists() {
		priority = new ArrayList<ArrayList<Vehicle>>();
		for (int i = 0; i < 5; i++) {
			priority.add(new ArrayList<>());
		}
	}

	@Override
	public boolean tryToEnterInner(Vehicle vehicle) {
		while (true) {
			lock.lock();
			int currPr = vehicle.getPriority();
			if (!priority.get(currPr).contains(vehicle)) {
				priority.get(currPr).add(vehicle);
			}
			while (!checkHighPrior(currPr) || !checkTunnels(vehicle)) {// if its not its turn or if it can't go in
				try {
					exitedFromTunnel.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					lock.unlock();
				}
			}
			// need to put successful entry code in else block,
			priority.get(currPr).remove(vehicle);
			lock.unlock();
			return true;
		}

	}


	/**
	 * breaks loop by returning false if the vehicle entered
	 * 
	 * @param vehicle
	 * @return
	 */
	private boolean checkTunnels(Vehicle vehicle) {
		for (Tunnel tunnel : tunnels) {
			if (vehicleMap == null)
				System.out.println("null");
			if (tunnel.tryToEnterInner(vehicle)) {// use try to enter/exit inner
				// add to tunnel map
				vehicleMap.put(vehicle, tunnel);// if it enters the tunnel remember which tunnel
				return true;
			}
		}
		return false;
	}

	/**
	 * returns true if its its turn goes from max to before curr
	 * 
	 * @param curr
	 * @return
	 */
	private boolean checkHighPrior(int curr) {
		for (int i = 4; i > curr; i--) {
			if (!priority.get(i).isEmpty()) {
				return false;// if not empty return false
			}
		}
		return true;// if all before are true return true
	}

	@Override
	public void exitTunnelInner(Vehicle vehicle) {
		lock.lock();
		Tunnel correctTunnel = vehicleMap.get(vehicle);
		correctTunnel.exitTunnelInner(vehicle);
		vehicleMap.remove(vehicle);
		exitedFromTunnel.signalAll();
		lock.unlock();
	}

}