package francotobias.tdpproyecto.Helpers;


import com.google.android.gms.maps.model.LatLng;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class LocationUtils {

	public static float walkingDistance(LatLng from, LatLng to) {
		LatLng midPoint = new LatLng(from.latitude, to.longitude);
		return (float) (computeDistanceBetween(from, midPoint) + computeDistanceBetween(midPoint, to));
	}

}
