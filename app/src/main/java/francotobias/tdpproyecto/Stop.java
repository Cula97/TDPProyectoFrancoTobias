package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

public class Stop {
	protected LatLng location;
	protected boolean isGo;

	public Stop(double lat, double lng, boolean go) {
		location = new LatLng(lat, lng);
		isGo = go;
	}

	public LatLng getLocation() {
		return location;
	}

	public boolean isGo() {
		return isGo;
	}
}
