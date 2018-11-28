package francotobias.tdpproyecto.PathModel;

import com.google.android.gms.maps.model.LatLng;

public class Stop {
	protected final LatLng location;
	protected final boolean isGo;
	protected Section section;

	public Stop(double lat, double lng, boolean go) {
		location = new LatLng(lat, lng);
		isGo = go;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	public Section getSection() {
		return section;
	}

	public LatLng getLocation() {
		return location;
	}

	public boolean isGo() {
		return isGo;
	}
}
