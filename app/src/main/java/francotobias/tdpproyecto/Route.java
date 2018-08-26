package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

public class Route {
	protected List<Section> routeSectionGo, routeSectionReturn;
	protected List<LatLng> routeGo, routeReturn;
	protected Line line;
	protected List<Stop> stops;

	public Route(Line l, List<LatLng> rGo, List<LatLng> rReturn) {
		line = l;
		l.setRoute(this);
		routeGo = rGo;
		routeReturn = rReturn;
		routeSectionGo = new LinkedList<>();
		routeSectionReturn = new LinkedList<>();

		for (int i = 0; i < rGo.size() - 1; i++)
			routeSectionGo.add(new Section(this, rGo.get(i), rGo.get(i + 1), true));

		for (int i = 0; i < rReturn.size() - 1; i++)
			routeSectionReturn.add(new Section(this, rReturn.get(i), rReturn.get(i + 1), false));
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
		// TODO: falta asosciar las paradas a las secciones, no se si en este metodo o en LineManager
		stops = s;
	}

	public List<Section> getSectionsGo() {
		return routeSectionGo;
	}

	public List<Section> getSectionsReturn() {
		return routeSectionReturn;
	}

	public List<LatLng> getGo() {
		return routeGo;
	}

	public List<LatLng> getReturn() {
		return routeReturn;
	}

	public void addStop(Stop s) {
		stops.add(s);
	}

	public void removeStop(Stop s) {
		stops.remove(s);
	}
}
