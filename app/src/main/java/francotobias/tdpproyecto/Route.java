package francotobias.tdpproyecto;

import android.location.Location;

import java.util.List;

public class Route {
	protected List<Location> routeGo, routeReturn;
	protected RouteNumber line;

	public Route(List<Location> rGo, List<Location> rReturn) {
		routeGo = rGo;
		routeReturn = rReturn;
	}

	public RouteNumber getLine() {
		return line;
	}

	public void setLine(RouteNumber l) {
		line = l;
	}
}
