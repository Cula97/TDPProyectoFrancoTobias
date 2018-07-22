package francotobias.tdpproyecto;

import android.location.Location;

import java.util.List;

public class Line {
	protected List<Bus> fleet;
	protected Route route;
	protected List<Stop> stops;
	protected String routeID;
	protected String company;

	public Line(String ID, Route r) {
		routeID = ID;
		route = r;
	}

	public Line(String ID, Route r, List<Bus> f, List<Stop> s) {
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

	public void addStop(Stop s) {
		stops.add(s);
	}

	public void removeStop(Stop s) {
		stops.remove(s);
	}
}
