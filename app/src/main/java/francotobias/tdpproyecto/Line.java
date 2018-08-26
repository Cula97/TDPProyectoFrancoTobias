package francotobias.tdpproyecto;

import android.location.Location;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Line {
	private static final long FLEET_UPDATE_TIME = 20000; // Update every 20 sec at max
	public final String lineID;
	protected Map<String, Bus> fleet = new HashMap<>();
	protected Route route;
	private long lastUpdateTime;

	public Line(String ID) {
		lineID = ID;
	}

	public String getID() {
		return lineID;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route r) {
		route = r;
	}

	public Map<String, Bus> getFleet() {
		return fleet;
	}


	/** TODO: Refactory de esto, este metodo no deberia encargarse de chequear si hay que
	 *        hay que actulizar las posiciones de los colectivos, eso deberia ser responsabilidad
	 *        del BusManager.
	 */
	public Iterable<Bus> updateBuses() {
		if (fleet.isEmpty()) {
			BusManager.addBuses(this);
			lastUpdateTime = Calendar.getInstance().getTimeInMillis();
		}

		if (Calendar.getInstance().getTimeInMillis() - lastUpdateTime > FLEET_UPDATE_TIME) {
			BusManager.updateBuses(this);
			lastUpdateTime = Calendar.getInstance().getTimeInMillis();
		}

		return fleet.values();
	}

	public Stop[] getClosestStops(Location location) {
		List<Stop> stops = route.getStops();
		Stop[] toReturn = new Stop[2];
		float dist, minDistGo = (float) 1e5, minDistRet = (float) 1e5;
		Location stop = new Location("");

		for (Stop s : stops) {
			stop.setLatitude(s.getLocation().latitude);
			stop.setLatitude(s.getLocation().longitude);
			dist = location.distanceTo(stop);
			if (dist < minDistGo && s.isGo()) {
				minDistGo = dist;
				toReturn[0] = new Stop(stop.getLatitude(), stop.getLongitude(), true);
			}

			if (dist < minDistRet && !s.isGo()) {
				minDistRet = dist;
				toReturn[1] = new Stop(stop.getLatitude(), stop.getLongitude(), false);
			}
		}

		return toReturn;
	}

	public Bus[] getClosestBuses(Location location) {
		Bus[] toReturn = new Bus[2];
		float dist, minDistGo = (float) 1e5, minDistRet = (float) 1e5;

		for (Bus bus : updateBuses()) {
			dist = location.distanceTo(bus.getLocation());
			if (dist < minDistGo && bus.isGo()) {
				minDistGo = dist;
				toReturn[0] = bus;
			}
			if (dist < minDistRet && !bus.isGo()) {
				minDistRet = dist;
				toReturn[1] = bus;
			}
		}

		return toReturn;
	}

	public void addBus(Bus b) {
		fleet.put(b.busID, b);
	}

	public void removeBus(Bus b) {
		fleet.remove(b.busID);
	}

}
