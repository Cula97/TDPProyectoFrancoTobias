package francotobias.tdpproyecto;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {

	public static Location latLngToLocation(LatLng latLng, String provider) {
		if (provider == null)
			provider = "";

		Location location = new Location(provider);
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);

		return location;
	}

	public static Location latLngToLocation(LatLng latLng) {
		return latLngToLocation(latLng, null);
	}
}
