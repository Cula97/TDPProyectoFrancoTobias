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
	protected boolean validStops;
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


	/**
	 * La lista de entrada contiene las paradas de ida en el sentido del recorrido (de ida)
	 * y las de vuelta en sentido opuesto al recorrido (arrancan al final de trayecto de vuelta)
	 */
	public void setStops(List<Stop> s) {
		stops = s;

		// TODO: algunas paradas se asocian mal pq hay tramos sin paradas, agregar distancia minima a inicio y final del tramo.
		// Associates stops in te go route to the corresponding sections
		int stopIndex = s.size(), goIndex = 0, retIndex = 0;
		float  newDistance, lastDistance = 100000;

		Section sectionGo = routeSectionGo.get(goIndex);
		Section sectionRet = routeSectionReturn.get(retIndex);
		Stop stop;

		Location stopLoc;
		Location epLoc = BusManager.latLngToLocation(sectionGo.endPoint, "");

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
				// Orden de paradas invalido
				if (++goIndex >= routeSectionGo.size()) {
					Log.d("pi " + line.lineID + " defasada",  ((Integer)routeSectionGo.size()).toString()+"    "+ ((Integer)goIndex).toString());
					sectionGo = routeSectionGo.get(0);      // for debugging
					validStops = false;
				//	return;
				}
				else
					sectionGo = routeSectionGo.get(goIndex);

				epLoc = BusManager.latLngToLocation(sectionGo.endPoint, "");
				lastDistance = stopLoc.distanceTo(epLoc);
			}

			sectionGo.addStop(stop);
		}


		lastDistance = 100000;
		epLoc = BusManager.latLngToLocation(sectionRet.endPoint, "");
		// Associates stops in the ret route to the corresponding sections
		for  (int i = s.size() - 1; i >= stopIndex; i--) {
			stop = s.get(i);
			stopLoc = BusManager.latLngToLocation(stop.location, "");
			newDistance = stopLoc.distanceTo(epLoc);

			if (newDistance < lastDistance)
				lastDistance = newDistance;
			else {
				// Orden deparadas invalido
				if (++retIndex >= routeSectionReturn.size()) {
					Log.d("pv " + line.lineID + " defasada",  ((Integer)routeSectionReturn.size()).toString()+"    "+ ((Integer)retIndex).toString());
					sectionRet = routeSectionReturn.get(0);     // for debugging
					validStops = false;
				//	return;
				}
				else
					sectionRet = routeSectionReturn.get(retIndex);

				epLoc = BusManager.latLngToLocation(sectionRet.endPoint, "");
				lastDistance = stopLoc.distanceTo(epLoc);
			}

			sectionRet.addStop(stop);
		}

		validStops = true;
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

//	public boolean

	public void addStop(Stop s) {
		stops.add(s);
	}

	public void removeStop(Stop s) {
		stops.remove(s);
	}
}
