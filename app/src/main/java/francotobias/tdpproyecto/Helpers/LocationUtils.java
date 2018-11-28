package francotobias.tdpproyecto.Helpers;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class LocationUtils {

	@Deprecated
	public static Location latLngToLocation(LatLng latLng, String provider) {
		if (provider == null)
			provider = "";

		Location location = new Location(provider);
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);

		return location;
	}

	@Deprecated
	public static Location latLngToLocation(LatLng latLng) {
		return latLngToLocation(latLng, null);
	}

	public static float walkingDistance(LatLng from, LatLng to) {
		LatLng midPoint = new LatLng(from.latitude, to.longitude);
		return (float) (computeDistanceBetween(from, midPoint) + computeDistanceBetween(midPoint, to));
	}
}
