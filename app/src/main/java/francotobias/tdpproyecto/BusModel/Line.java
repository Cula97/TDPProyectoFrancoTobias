package francotobias.tdpproyecto.BusModel;


import francotobias.tdpproyecto.PathModel.Route;

public class Line {
	public final String lineID;
	protected Route route;

	public Line(String ID) {
		lineID = ID;
	}

	@Deprecated
	public String getID() {
		return lineID;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route r) {
		route = r;
	}

}
