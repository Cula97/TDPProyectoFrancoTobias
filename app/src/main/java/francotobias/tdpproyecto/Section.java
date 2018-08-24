package francotobias.tdpproyecto;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Section {
	private Route route;
	public final LatLng startPoint;
	public final LatLng endPoint;
	public final int bearing;
	public final float size;
	public final boolean isGo;
	private List<Stop> stops;
	private List<Float> distances;


	public Section(Route route, LatLng startPoint, LatLng endPoint, boolean isGo) {
		this.route = route;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.isGo = isGo;

		Location start = new Location("");
		Location end = new Location("");
		start.setLatitude(startPoint.latitude);
		start.setLongitude(startPoint.longitude);
		end.setLatitude(endPoint.latitude);
		end.setLongitude(endPoint.longitude);
		bearing = (int) start.bearingTo(end);
		size = start.distanceTo(end);
	}
}
