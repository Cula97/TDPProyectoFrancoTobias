package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

public class Stop {
	public final LatLng location;
	public final boolean isGo;
	private Section section;

	public Stop(double lat, double lng, boolean go) {
		location = new LatLng(lat, lng);
		isGo = go;
	}

	@Deprecated
	public LatLng getLocation() {
		return location;
	}

	@Deprecated
	public boolean isGo() {
		return isGo;
	}
}
