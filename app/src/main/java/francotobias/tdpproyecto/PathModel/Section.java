package francotobias.tdpproyecto.PathModel;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

import francotobias.tdpproyecto.BusModel.BusManager;

public class Section {
	public final LatLng startPoint;
	public final LatLng endPoint;
	public final int bearing;
	public final float size;
	public final boolean isGo;
	private Route route;
	private List<Stop> stops;       // Si no guardamos distancias y no importa el orden podria ser un set
//	private List<Float> distances;  // Innecesario? se pueden computar las distancias de la parada al principio y final del tramo


	public Section(Route route, LatLng startPoint, LatLng endPoint, boolean isGo) {
		this.route = route;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.isGo = isGo;

		Location start = BusManager.latLngToLocation(startPoint, null);
		Location end = BusManager.latLngToLocation(endPoint, null);

		bearing = (int) start.bearingTo(end);
		size = start.distanceTo(end);
	}

	public void addStop(Stop stop) {
		stop.setSection(this);
		Stop lastStop;

		if (stops == null) {
			stops = new LinkedList<>();     // ArrayList?
		//	distances = new LinkedList<>();
		//	distances.add(0f);      // This value will be removed

			lastStop = new Stop(startPoint.latitude, startPoint.longitude, stop.isGo);
		}
		else
			lastStop = stops.get(stops.size() - 1);

		Location stopLoc = BusManager.latLngToLocation(stop.location, null);
		Location lastStopLoc = BusManager.latLngToLocation(lastStop.location, null);
		Location end = BusManager.latLngToLocation(endPoint, null);

		stops.add(stop);
	//	distances.remove(distances.size() - 1);         // Remove distance from previous last stop to endPoint
	//	distances.add(lastStopLoc.distanceTo(stopLoc));   // Add distance from previous last stop to new last stop
	//	distances.add(stopLoc.distanceTo(end));           // Add dsitance from new last stop to endPoint
	}

	public void clearStops() {
		stops = null;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public Route getRoute() {
		return route;
	}
}
