package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

public class Route {
	protected List<Section> routeGo, routeReturn;
	protected Line line;
	protected List<Stop> stops;

	public Route(Line l, List<LatLng> rGo, List<LatLng> rReturn) {
		line = l;
		l.setRoute(this);
		routeGo = new LinkedList<>();
		routeReturn = new LinkedList<>();

		for (int i = 0; i < rGo.size() - 1; i++)
			routeGo.add(new Section (this, rGo.get(i), rGo.get(i+1), true));

		for (int i = 0; i < rReturn.size() - 1; i++)
			routeReturn.add(new Section (this, rReturn.get(i), rReturn.get(i+1), false));
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

	public List<LatLng> getGo() {
		List<LatLng> go = new LinkedList<>();
		go.add(routeGo.get(0).startPoint);

		for (Section section : routeGo)
			go.add(section.endPoint);

		return go;
	}

	public List<LatLng> getReturn() {
		List<LatLng> ret = new LinkedList<>();
		ret.add(routeReturn.get(0).startPoint);

		for (Section section : routeReturn)
			ret.add(section.endPoint);

		return ret;
	}

	public void addStop(Stop s) {
		stops.add(s);
	}

	public void removeStop(Stop s) {
		stops.remove(s);
	}
}
