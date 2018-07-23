package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
	protected List<LatLng> routeGo, routeReturn;
	protected Line line;
	protected List<Stop> stops;

	public Route(Line l, List<LatLng> rGo, List<LatLng> rReturn) {
		line = l;
		l.setRoute(this);
		routeGo = rGo;
		routeReturn = rReturn;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line l) {
		line = l;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> s) {
		stops = s;
	}

	public void addStop(Stop s) {
		stops.add(s);
	}

	public void removeStop(Stop s) {
		stops.remove(s);
	}
}
