package francotobias.tdpproyecto;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

public class Section {
	public final LatLng startPoint;
	public final LatLng endPoint;
	public final int bearing;
	public final float size;
	public final boolean isGo;
	private Route route;
	private List<Stop> stops;
	private List<Float> distances;


	public Section(Route route, LatLng startPoint, LatLng endPoint, boolean isGo) {
		this.route = route;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.isGo = isGo;

		Location start = BusManager.latLngToLocation(startPoint, "");
		Location end = BusManager.latLngToLocation(endPoint, "");

		bearing = (int) start.bearingTo(end);
		size = start.distanceTo(end);
	}

	public void addStop(Stop stop) {
		Stop lastStop;

		if (stops == null) {
			stops = new LinkedList<>();
			distances = new LinkedList<>();
			distances.add(0f);      // This value will be removed

			lastStop = new Stop(startPoint.latitude, startPoint.longitude, stop.isGo);
		}
		else
			lastStop = stops.get(stops.size() - 1);

		Location stopLoc = BusManager.latLngToLocation(stop.location, "");
		Location lastStopLoc = BusManager.latLngToLocation(lastStop.location, "");
		Location end = BusManager.latLngToLocation(endPoint, "");

		stops.add(stop);
		distances.remove(distances.size() - 1);         // Remove distance from previous last stop to endPoint
		distances.add(lastStopLoc.distanceTo(stopLoc));   // Add distance from previous last stop to new last stop
		distances.add(stopLoc.distanceTo(end));           // Add dsitance from new last stop to endPoint
	}

	public List<Stop> getStops() {
		return stops;
	}

}
