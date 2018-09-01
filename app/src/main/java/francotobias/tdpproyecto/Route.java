package francotobias.tdpproyecto;

import android.location.Location;
import android.util.Log;

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
		// TODO: codigo repetido, revisar.
		int stopIndex = s.size(), goIndex = 0, retIndex = 0;
		float  newDistance, lastDistance = 100000;

		Section sectionGo = routeSectionGo.get(goIndex);
		Section sectionRet = routeSectionReturn.get(retIndex);
		Stop stop;

		Location stopLoc, epLoc = BusManager.latLngToLocation(sectionGo.endPoint, "");
		// Associates stops in te go route to the corresponding sections
		for (int i = 0; i < stopIndex; i++) {
			stop = s.get(i);

			if (!stop.isGo) {
				stopIndex = s.indexOf(stop);
				break;
			}

			stopLoc = BusManager.latLngToLocation(stop.location, "");
			newDistance = stopLoc.distanceTo(epLoc);

			if (newDistance < lastDistance)
				lastDistance = newDistance;
			else {
				// TODO: fix dis
				int aux = routeSectionGo.size() - 1;
				sectionGo = routeSectionGo.get(Math.min(++goIndex, aux));                // <-- FIX!!!
				if (aux < goIndex)
					Log.d("parada ida",((Integer) goIndex).toString());
				epLoc = BusManager.latLngToLocation(sectionGo.endPoint, "");
				lastDistance = stopLoc.distanceTo(epLoc);
			}
			sectionGo.addStop(stop);
		}


		lastDistance = 100000;
		epLoc = BusManager.latLngToLocation(sectionRet.endPoint, "");
		// Associates stops in te ret route to the corresponding sections
		for  (int i = s.size() - 1; i >= stopIndex; i--) {
			stop = s.get(i);
			stopLoc = BusManager.latLngToLocation(stop.location, "");
			newDistance = stopLoc.distanceTo(epLoc);

			if (newDistance < lastDistance)
				lastDistance = newDistance;
			else {
				//TODO: fix dis too
				int aux = routeSectionReturn.size() - 1;
				sectionRet = routeSectionReturn.get(Math.min(++retIndex, aux));        // <-- FIX!!!
				if (aux < retIndex)
					Log.d("parada vuelta",((Integer) retIndex).toString());
				epLoc = BusManager.latLngToLocation(sectionRet.endPoint, "");
				lastDistance = stopLoc.distanceTo(epLoc);
			}
			sectionRet.addStop(stop);
		}

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
