package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
	protected List<LatLng> routeGo, routeReturn;
	protected Line line;

	public Route(List<LatLng> rGo, List<LatLng> rReturn) {
		routeGo = rGo;
		routeReturn = rReturn;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line l) {
		line = l;
	}
}
