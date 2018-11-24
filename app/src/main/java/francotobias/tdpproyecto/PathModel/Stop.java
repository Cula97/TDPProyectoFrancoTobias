package francotobias.tdpproyecto.PathModel;

import com.google.android.gms.maps.model.LatLng;

import francotobias.tdpproyecto.PathModel.Section;

public class Stop {
	public final LatLng location;
	public final boolean isGo;
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

	@Deprecated
	public LatLng getLocation() {
		return location;
	}

	@Deprecated
	public boolean isGo() {
		return isGo;
	}
}
