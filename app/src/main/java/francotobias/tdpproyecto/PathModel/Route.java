package francotobias.tdpproyecto.PathModel;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import francotobias.tdpproyecto.BusModel.BusManager;
import francotobias.tdpproyecto.BusModel.Line;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class Route {
	public static final float INVALID_DISTANCE = -1;
	protected static double MIN_DISTANCE_THRESHOLD = 100;   // Podria ser menor si la data fuera mejor
	protected List<Section> routeSectionGo, routeSectionReturn;
	protected Line line;
	protected boolean validStops = true;
	protected boolean insertedStops = false;


	public Route(Line l, List<LatLng> rGo, List<LatLng> rReturn) {
		line = l;
		l.setRoute(this);
		routeSectionGo = new LinkedList<>();
		routeSectionReturn = new LinkedList<>();

		Iterator<LatLng> routeIterator = rGo.iterator();
		LatLng end, start = routeIterator.next();

		while (routeIterator.hasNext()) {
			end = routeIterator.next();
			routeSectionGo.add(new Section(this, start, end, true));
			start = end;
		}

		routeIterator = rReturn.iterator();
		start = routeIterator.next();

		while (routeIterator.hasNext()) {
			end = routeIterator.next();
			routeSectionReturn.add(new Section(this, start, end, false));
			start = end;
		}
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line l) {
		line = l;
	}

	public List<Stop> getStops() {
		List<Stop> stops = new LinkedList<>();

		for (Section section : routeSectionGo) {
			List<Stop> s = section.getStops();
			if (s != null)
				stops.addAll(section.getStops());
		}


		for (Section section : routeSectionReturn) {
			List<Stop> s = section.getStops();
			if (s != null)
				stops.addAll(section.getStops());
		}

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

		insertedStops = true;
		LinkedList<Stop> stopsGo = new LinkedList<>();
		LinkedList<Stop> stopsRet = new LinkedList<>();

		// Detect and correct inverted stops
		boolean invertedStops = false;
		Stop firstStop = stops.get(0);
		Section firstSection, lastSection;

		firstSection = (firstStop.isGo) ?
				routeSectionGo.get(0) :
				routeSectionReturn.get(0);

		lastSection = (firstStop.isGo) ?
				routeSectionGo.get(routeSectionGo.size() - 1) :
				routeSectionReturn.get(routeSectionReturn.size() - 1);

		double distanceToFirstSection = PolyUtil.distanceToLine(firstStop.getLocation(), firstSection.getStartPoint(), firstSection.getEndPoint());
		double distanceToLastSection = PolyUtil.distanceToLine(firstStop.getLocation(), lastSection.getStartPoint(), lastSection.getEndPoint());

		if (distanceToFirstSection > distanceToLastSection) {
			invertedStops = true;
			Log.d("Paradas Invertidas", line.lineID);
		}

		for (Stop stop : stops)
			if (stop.isGo)
				if (invertedStops)
					stopsGo.addFirst(stop);
				else
					stopsGo.addLast(stop);
			else if (invertedStops)
				stopsRet.addLast(stop);
			else
				stopsRet.addFirst(stop);

		addStopsToSections(stopsGo, routeSectionGo);
		addStopsToSections(stopsRet, routeSectionReturn);
	}


	private void addStopsToSections(List<Stop> stops, List<Section> sections) {
		Iterator<Section> sectionIterator = sections.iterator();
		Iterator<Stop> stopIterator = stops.iterator();
		float distance, lastDistance = -1;
		double distanceToLine = -1;
		Section section = sections.get(0);
		Location stopLocation, epLocation = BusManager.latLngToLocation(section.getEndPoint(), null);
		Stop stop;

		while (stopIterator.hasNext()) {
			stop = stopIterator.next();
			stopLocation = BusManager.latLngToLocation(stop.getLocation(), null);
			distance = stopLocation.distanceTo(epLocation);         // Mas barato

			if (distance > lastDistance)
				while (sectionIterator.hasNext()) {
					section = sectionIterator.next();
					distanceToLine = PolyUtil.distanceToLine(stop.getLocation(), section.getStartPoint(), section.getEndPoint());

					if (distanceToLine <= MIN_DISTANCE_THRESHOLD) {
						epLocation = BusManager.latLngToLocation(section.getEndPoint(), null);
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


	public Stop[] getClosestStops(LatLng point) {
		Stop[] toReturn = new Stop[2];
		float dist, minDistGo = 1e5f, minDistRet = 1e5f;

		for (Stop s : getStops()) {
			dist = (float) computeDistanceBetween(s.getLocation(), point);

			if (dist < minDistGo && s.isGo) {
				minDistGo = dist;
				toReturn[0] = s;
				continue;
			}

			if (dist < minDistRet && !s.isGo) {
				minDistRet = dist;
				toReturn[1] = s;
			}
		}

		return toReturn;
	}


	private Section[] getClosestSections(LatLng latLng) {
		double dist, minDist = 10000;
		Section[] toRetrun = new Section[2];

		for (Section section : routeSectionGo) {
			dist = PolyUtil.distanceToLine(latLng, section.getStartPoint(), section.getEndPoint());
			if (dist < minDist) {
				minDist = dist;
				toRetrun[0] = section;
				if (dist < MIN_DISTANCE_THRESHOLD)
					break;
			}
		}

		minDist = 10000;
		for (Section section : routeSectionReturn) {
			dist = PolyUtil.distanceToLine(latLng, section.getStartPoint(), section.getEndPoint());
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
			return BusManager.latLngToLocation(start.getLocation(), null).distanceTo(BusManager.latLngToLocation(end.getLocation(), null));

		float distance = 0;

		// Distance from start to end of section
		Location locationStop = BusManager.latLngToLocation(start.getLocation(), null);
		Location locationOnSection = BusManager.latLngToLocation(sectionStart.getEndPoint(), null);
		distance += locationStop.distanceTo(locationOnSection);

		// Distance from start of last section to end
		locationStop = BusManager.latLngToLocation(end.getLocation(), null);
		locationOnSection = BusManager.latLngToLocation(sectionEnd.getStartPoint(), null);
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
			distance += sectionStart.getSize();
		}

		// Iterator for the sections that contain the ending stop if it wasn't with the other ones
		iterator = !start.isGo ?
				routeSectionGo.iterator() :
				routeSectionReturn.iterator();

		while (iterator.hasNext()) {
			sectionStart = iterator.next();
			if (sectionStart == sectionEnd)
				return distance;
			distance += sectionStart.getSize();
		}

		return INVALID_DISTANCE;
	}

	private List<LatLng> getLatLngList(List<Section> sections) {
		List<LatLng> latLngList = new LinkedList<>();

		latLngList.add(sections.get(1).getStartPoint());
		for (Section section : sections)
			latLngList.add(section.getEndPoint());

		return latLngList;
	}

	public List<LatLng> getGo() {
		return getLatLngList(routeSectionGo);
	}

	public List<LatLng> getReturn() {
		return getLatLngList(routeSectionReturn);
	}

	public List<Section> getSectionsGo() {
		return routeSectionGo;
	}

	public List<Section> getSectionsReturn() {
		return routeSectionReturn;
	}

	// Needed to comupte travel distance
	public boolean validStops() {
		return validStops && insertedStops;
	}

}
