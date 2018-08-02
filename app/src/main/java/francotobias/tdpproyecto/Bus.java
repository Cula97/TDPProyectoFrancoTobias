package francotobias.tdpproyecto;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class Bus {
	private static final int MINIMUM_ANGLE_THRESHOLD = 15;

	protected Location location;
	protected Line line;
	protected String busID;


	public Bus(String busID, Line line, Location location) {
		this.busID = busID;
		this.line = line;
		this.location = location;
	}

	public boolean isGo() {
		List<LatLng> goRoute = line.getRoute().getGo();
		List<LatLng> returnRoute = line.getRoute().getReturn();

		Location minGo =  new Location(""), minRet = new Location(""), routePoint = new Location("");
		LatLng minCoordGo = null, minCoordRet = null;
		double dist, minDist = 1e10;

		for (LatLng routeCoord : goRoute) {
			routePoint.setLatitude(routeCoord.latitude);
			routePoint.setLongitude(routeCoord.longitude);
			dist = location.distanceTo(routePoint);
			if (minDist > dist) {
				minCoordGo = routeCoord;
				minDist = dist;
				minGo.setLatitude(routePoint.getLatitude());
				minGo.setLongitude(routePoint.getLongitude());
			}
		}

		minDist = 1e10;
		for (LatLng routeCoord : returnRoute) {
			routePoint.setLatitude(routeCoord.latitude);
			routePoint.setLongitude(routeCoord.longitude);
			dist = location.distanceTo(routePoint);
			if (minDist > dist) {
				minCoordRet = routeCoord;
				minDist = dist;
				minRet.setLatitude(routePoint.getLatitude());
				minRet.setLongitude(routePoint.getLongitude());
			}
		}

		Location minGoPrev = new Location("");
		Location minGoNext = new Location("");
		Location minRetPrev = new Location("");
		Location minRetNext = new Location("");

		int goBearing1 = 360, goBearing2 = 360, retBearing1 = 360, retBearing2 = 360;

		int indexMinCoord = goRoute.indexOf(minCoordGo);
		if (indexMinCoord > 0) {
			minGoPrev.setLatitude( goRoute.get(indexMinCoord - 1).latitude );
			minGoPrev.setLongitude( goRoute.get(indexMinCoord - 1).longitude );
			goBearing1 = (int) minGoPrev.bearingTo(minGo);
		}
		if (indexMinCoord < goRoute.size()) {
			minGoNext.setLatitude( goRoute.get(indexMinCoord + 1).latitude );
			minGoNext.setLongitude( goRoute.get(indexMinCoord + 1).longitude );
			goBearing2 = (int) minGo.bearingTo(minGoNext);
		}

		indexMinCoord = returnRoute.indexOf(minCoordRet);
		if (indexMinCoord > 0) {
			minRetPrev.setLatitude( returnRoute.get(indexMinCoord - 1).latitude );
			minRetPrev.setLongitude( returnRoute.get(indexMinCoord - 1).longitude );
			retBearing1 = (int) minRetPrev.bearingTo(minRet);
		}
		if (indexMinCoord < returnRoute.size()) {
			minRetNext.setLatitude( returnRoute.get(indexMinCoord + 1).latitude );
			minRetNext.setLongitude( returnRoute.get(indexMinCoord + 1).longitude );
			retBearing2 = (int) minRet.bearingTo(minRetNext);
		}

		int minGoBearing = Math.min(
				Math.abs(goBearing1 - (int) location.getBearing()),
				Math.abs(goBearing2 - (int) location.getBearing()));

		int minRetBearing = Math.min(
				Math.abs(retBearing1 - (int) location.getBearing()),
				Math.abs(retBearing2 - (int) location.getBearing()));

		if (minGoBearing < minRetBearing && minGoBearing < MINIMUM_ANGLE_THRESHOLD)
			return true;
		else
			if (minRetBearing < MINIMUM_ANGLE_THRESHOLD)
				return false;

		Log.w("Bearing", "Angulo demasiado impreciso para calcular direccion.");
		return false;
	}

}
