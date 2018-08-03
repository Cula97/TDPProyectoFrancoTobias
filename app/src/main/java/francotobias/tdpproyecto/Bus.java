package francotobias.tdpproyecto;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.List;


public class Bus {
	private static final int MINIMUM_ANGLE_THRESHOLD = 15;
	private static final int DIRECTION_CHECK_TIME = 600000; // Check every 10 mins

	protected Location location;
	protected Line line;
	protected String busID;
	protected boolean isGo;
	protected long lastDirectionCheckTime;


	public Bus(String busID, Line line, Location location) {
		this.busID = busID;
		this.line = line;
		this.location = location;
	}

	public String getBusID() {
		return busID;
	}

	public Location getLocation() {
		return location;
	}

	public boolean isGo() {
		if (lastDirectionCheckTime != 0 &&
				Calendar.getInstance().getTimeInMillis() - lastDirectionCheckTime < DIRECTION_CHECK_TIME)
			return isGo;

		List<LatLng> goRoute = line.getRoute().getGo();
		List<LatLng> returnRoute = line.getRoute().getReturn();

		Location minGo = new Location(""), minRet = new Location(""), routePoint = new Location("");
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
			minGoPrev.setLatitude(goRoute.get(indexMinCoord - 1).latitude);
			minGoPrev.setLongitude(goRoute.get(indexMinCoord - 1).longitude);
			goBearing1 = (int) minGoPrev.bearingTo(minGo);
		}
		if (indexMinCoord < goRoute.size()) {
			minGoNext.setLatitude(goRoute.get(indexMinCoord + 1).latitude);
			minGoNext.setLongitude(goRoute.get(indexMinCoord + 1).longitude);
			goBearing2 = (int) minGo.bearingTo(minGoNext);
		}

		indexMinCoord = returnRoute.indexOf(minCoordRet);
		if (indexMinCoord > 0) {
			minRetPrev.setLatitude(returnRoute.get(indexMinCoord - 1).latitude);
			minRetPrev.setLongitude(returnRoute.get(indexMinCoord - 1).longitude);
			retBearing1 = (int) minRetPrev.bearingTo(minRet);
		}
		if (indexMinCoord < returnRoute.size()) {
			minRetNext.setLatitude(returnRoute.get(indexMinCoord + 1).latitude);
			minRetNext.setLongitude(returnRoute.get(indexMinCoord + 1).longitude);
			retBearing2 = (int) minRet.bearingTo(minRetNext);
		}

		int minGoBearing = Math.min(
				Math.abs(
						Math.abs(goBearing1 - 180) -
								Math.abs((int) location.getBearing() - 180)),
				Math.abs(
						Math.abs(goBearing2 - 180) -
								Math.abs((int) location.getBearing() - 180)));

		int minRetBearing = Math.min(
				Math.abs(
						Math.abs(retBearing1 - 180) -
								Math.abs((int) location.getBearing() - 180)),
				Math.abs(
						Math.abs(retBearing2 - 180) -
								Math.abs((int) location.getBearing() - 180)));

		if (minGoBearing < minRetBearing && minGoBearing < MINIMUM_ANGLE_THRESHOLD) {
			lastDirectionCheckTime = Calendar.getInstance().getTimeInMillis();
			isGo = true;
			return true;
		} else if (minRetBearing < MINIMUM_ANGLE_THRESHOLD) {
			lastDirectionCheckTime = Calendar.getInstance().getTimeInMillis();
			isGo = false;
			return false;
		}

		Log.w("Bearing", "Angulo demasiado impreciso para calcular direccion.");
		return false;
	}

}