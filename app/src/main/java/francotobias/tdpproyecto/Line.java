package francotobias.tdpproyecto;

import java.util.List;

public class Line {
	protected List<Bus> fleet;
	protected Route route;
	protected String routeID;
	protected String company;

	public Line(String ID) {
		routeID = ID;
		route = r;
	}

	public Line(String ID, Route r, List<Bus> f) {
		routeID = ID;
		route = r;
		fleet = f;
	}

	public void setRoute(Route r) {
		route = r;
	}

	public void addBus(Bus b) {
		fleet.add(b);
	}

	public void removeBus(Bus b) {
		fleet.remove(b);
	}

}
