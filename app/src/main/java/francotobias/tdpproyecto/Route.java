package francotobias.tdpproyecto;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Route {
	protected List<Section> routeSectionGo, routeSectionReturn;
	protected List<LatLng> routeGo, routeReturn;            // Se pueden computar
	protected Line line;
	protected boolean validStops = true;
	protected List<Stop> stops;                             // Se puede computar
	protected static double MIN_DISTANCE_THRESHOLD = 100;    // Podria ser menor si la data fuera mejor


	public Route(Line l, List<LatLng> rGo, List<LatLng> rReturn) {
		line = l;
		l.setRoute(this);
		routeGo = rGo;
		routeReturn = rReturn;
		routeSectionGo = new LinkedList<>();        // ArrayList?
		routeSectionReturn = new LinkedList<>();    // ArrayList?

		Iterator<LatLng> routeIterator = rGo.iterator();
		LatLng end, start = routeIterator.next();

		while (routeIterator.hasNext()) {
			end = routeIterator.next();
			routeSectionGo.add(new Section( this, start, end, true));
			start = end;
		}

		routeIterator = rReturn.iterator();
		start = routeIterator.next();

		while (routeIterator.hasNext()) {
			end = routeIterator.next();
			routeSectionReturn.add(new Section( this, start, end, false));
			start = end;
		}
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line l) {
		line = l;
	}

	// Se puede computar para no tener que guardar la lista
	public List<Stop> getStops() {
		return stops;
	}


	/**
	 * La lista de entrada contiene las paradas de ida en el sentido del recorrido (de ida)
	 * y las de vuelta en sentido opuesto al recorrido (arrancan al final de trayecto de vuelta).
	 * Algunas lineas tienen las paradas cargadas en el orden inverso y hay que corregirlo.
	 */
	public void setStops(List<Stop> stops) {
		if (stops.size() == 0) {
			Log.d("Sin paradas", line.lineID);
			validStops = false;
			return;
		}

		List<Stop> stopsGo = new LinkedList<>();
		List<Stop> stopsRet = new LinkedList<>();

		// Detect and correct inverted stops
		boolean invertedStops = false;
	//	Stop firstStop = stops.get(0);
	//	Section firstSection, lastSection;
	//	firstSection = firstStop.isGo ? routeSectionGo.get(0) : routeSectionReturn.get(0);
	//	lastSection = firstStop.isGo ? routeSectionGo.get(routeSectionGo.size() -1) : routeSectionReturn.get(routeSectionReturn.size() -1);
	//	double distanceToFirstSection = PolyUtil.distanceToLine(firstStop.location, firstSection.startPoint, firstSection.endPoint);
	//	double distanceToLastSection = PolyUtil.distanceToLine(firstStop.location, lastSection.startPoint, lastSection.endPoint);

	//	if (distanceToFirstSection > distanceToLastSection) {
	//		invertedStops = true;
	//		Log.d("Paradas Invertidas", line.lineID);
	//	}

		for (Stop stop : stops)
			if (stop.isGo)
				if (invertedStops)
					stopsGo.add(0, stop);
				else
					stopsGo.add(stop);
			else
				if (invertedStops)
					stopsRet.add(stop);
				else
					stopsRet.add(0, stop);

		this.stops = new LinkedList<>();
		this.stops.addAll(stopsGo);
		this.stops.addAll(stopsRet);

		addStopsToSections(stopsGo, routeSectionGo);
		addStopsToSections(stopsRet, routeSectionReturn);
	}


	private void addStopsToSections(List<Stop> stops, List<Section> sections) {
		Iterator<Section> sectionIterator = sections.iterator();
		Iterator<Stop> stopIterator = stops.iterator();
		float distance, lastDistance = -1;
		double distanceToLine = -1;
		Section section = sections.get(0);
		Location stopLocation, epLocation = BusManager.latLngToLocation(section.endPoint, null);
		Stop stop;

		while (stopIterator.hasNext()) {
			stop = stopIterator.next();
			stopLocation = BusManager.latLngToLocation(stop.location, null);
			distance = stopLocation.distanceTo(epLocation);         // Mas barato

			if (distance > lastDistance)
				while (sectionIterator.hasNext()) {
					section = sectionIterator.next();
					distanceToLine = PolyUtil.distanceToLine(stop.location, section.startPoint, section.endPoint);

					if (distanceToLine <= MIN_DISTANCE_THRESHOLD) {
						epLocation = BusManager.latLngToLocation(section.endPoint, null);
						distance = stopLocation.distanceTo(epLocation);
						break;
					}
				}

				// Debugging
				if (distanceToLine > MIN_DISTANCE_THRESHOLD && validStops) {
					Log.d("Paradas defasadas", line.lineID);
					validStops = false;
					//return;
				}

			lastDistance = distance;
			section.addStop(stop);
		}

		// Debugging
		if (validStops)
			Log.d("Paradas Validas", line.lineID);
	}


	// Mover a Line?
	private Section[] getClosestSectctions(LatLng latLng) {
		double dist, minDist = 10000;
		Section[] toRetrun = new Section[2];

		for (Section section : routeSectionGo) {
			dist = PolyUtil.distanceToLine(latLng, section.startPoint, section.endPoint);
			if (dist < minDist) {
				minDist = dist;
				toRetrun[0] = section;
				if (dist < MIN_DISTANCE_THRESHOLD)
					break;
			}
		}

		minDist = 10000;
		for (Section section : routeSectionReturn) {
			dist = PolyUtil.distanceToLine(latLng, section.startPoint, section.endPoint);
			if (dist < minDist) {
				minDist = dist;
				toRetrun[1] = section;
				if (dist < MIN_DISTANCE_THRESHOLD)
					break;
			}
		}

		return toRetrun;
	}


	public float distanceBetweenStops(Stop start, Stop end) {
		Section sectionStart = start.getSection();
		Section sectionEnd = end.getSection();

		if (sectionStart.getRoute() != sectionEnd.getRoute() ||
				sectionStart.getRoute() != this)
			return -1;

		if (sectionStart == sectionEnd)
			return BusManager.latLngToLocation(start.location, null).distanceTo(BusManager.latLngToLocation(end.location, null));

		float distance = 0;

		// Distance from start to end of section
		Location locationStop = BusManager.latLngToLocation(start.location, null);
		Location locationOnSection = BusManager.latLngToLocation(sectionStart.endPoint, null);
		distance += locationStop.distanceTo(locationOnSection);

		// Distance from start of last section to end
		locationStop = BusManager.latLngToLocation(end.location, null);
		locationOnSection = BusManager.latLngToLocation(sectionEnd.startPoint, null);
		distance += locationOnSection.distanceTo(locationStop);

		// Iterator for the sections with the starting stop
		Iterator<Section> iterator = start.isGo ?
				routeSectionGo.listIterator(routeSectionGo.indexOf(sectionStart)) :
				routeSectionReturn.listIterator(routeSectionReturn.indexOf(sectionStart));

		// Skip the section that contains the startig stop
		if (iterator.hasNext())
			iterator.next();

		while (iterator.hasNext()) {
			sectionStart = iterator.next();
			if (sectionStart == sectionEnd)
				return distance;
			distance += sectionStart.size;
		}

		// Iterator for the sections that contain the ending stop if it wasn't with the other ones
		iterator = !start.isGo ?
				routeSectionGo.iterator() :
				routeSectionReturn.iterator();

		while (iterator.hasNext()) {
			sectionStart = iterator.next();
			if (sectionStart == sectionEnd)
				return distance;
			distance += sectionStart.size;
		}

		return -1;
	}


	public void clearStops() {
		for (Section s : routeSectionGo)
			s.clearStops();

		for (Section s : routeSectionReturn)
			s.clearStops();

		stops = null;
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

	// Needed to comupte travel distance
	public boolean validStops() {
		return validStops && stops != null;
	}

}
