package francotobias.tdpproyecto;

import android.location.Location;

import java.util.List;

public class RouteNumber {
	protected List<Bus> fleet;
	protected Route route;
	protected List<Location> stops;
	protected String routeID;
	protected String company;


	public RouteNumber(String ID, Route r) {
		routeID = ID;
		route = r;
	}

	public RouteNumber(String ID, Route r, List<Bus> f, List<Location> s) {
		routeID = ID;
		route = r;
		fleet = f;
		stops = s;
	}


	public void addBus(Bus b) {
		fleet.add(b);
	}

	public void removeBus(Bus b) {
		fleet.remove(b);
	}

	public void addStop(Location l) {
		stops.add(l);
	}

	public void removeStop(Location l) {
		stops.remove(l);
	}

}
