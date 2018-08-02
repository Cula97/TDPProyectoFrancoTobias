package francotobias.tdpproyecto;

import java.util.List;

public class Line {
	protected List<Bus> fleet;
	protected Route route;
	protected String lineID;

	public Line(String ID) {
		lineID = ID;
	}

	public String getID() {
		return lineID;
	}

	public void setRoute(Route r) {
		route = r;
	}

	public Route getRoute() {
		return route;
	}

	public void initBuses() {
		BusFactory.addBuses(this);
	}

	public void addBus(Bus b) {
		fleet.add(b);
	}

	public void removeBus(Bus b) {
		fleet.remove(b);
	}

}
