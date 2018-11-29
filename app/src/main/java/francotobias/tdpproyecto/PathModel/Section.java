package francotobias.tdpproyecto.PathModel;


import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;
import static com.google.maps.android.SphericalUtil.computeHeading;

public class Section {
	private final LatLng startPoint;
	private final LatLng endPoint;
	private final int bearing;
	private final float size;
	private final boolean isGo;
	private Route route;
	private List<Stop> stops;


	public Section(Route route, LatLng startPoint, LatLng endPoint, boolean isGo) {
		this.route = route;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.isGo = isGo;

		bearing = (int) computeHeading(startPoint, endPoint);
		size = (float) computeDistanceBetween(startPoint, endPoint);
	}

	public void addStop(Stop stop) {
		stop.setSection(this);

		if (stops == null)
			stops = new LinkedList<>();

		stops.add(stop);
	}

	public List<Stop> getStops() {
		return stops;
	}

	public Route getRoute() {
		return route;
	}

	public float getSize() {
		return size;
	}

	public int getBearing() {
		return bearing;
	}

	public LatLng getStartPoint() {
		return startPoint;
	}

	public LatLng getEndPoint() {
		return endPoint;
	}

	public boolean isGo() {
		return isGo;
	}

}
