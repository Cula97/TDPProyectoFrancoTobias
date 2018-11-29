package francotobias.tdpproyecto.PathModel;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import francotobias.tdpproyecto.BusModel.Line;
import francotobias.tdpproyecto.BusModel.LineManager;

import static francotobias.tdpproyecto.Helpers.LocationUtils.walkingDistance;
import static francotobias.tdpproyecto.PathModel.Route.INVALID_DISTANCE;

public class Path implements Comparable<Path>{
//	private static final float MAX_WALKING_DISTANCE = 17000;
	private static final float WALKING_DETERRENT = 2.5f;

	private Stop firstStop;
	private Stop lastStops;
	private LatLng startLocation;
	private LatLng endLocation;
	private final float busDistance;
	private final float walkDistance;

	private Path(LatLng startLocation, LatLng endLocation,
	             Stop firstStop, Stop lastStops,
	             float walkDistance, float busDistance) {

		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.firstStop = firstStop;
		this.lastStops = lastStops;
		this.busDistance = busDistance;
		this.walkDistance = walkDistance;
	}


	public static Iterator<Path> shortestPaths(LatLng start, LatLng end) {
		SortedSet<Path> paths = new TreeSet<>();
		Path path;

		for (Line line : LineManager.lines()) {
			path = shortestPathAux(start, end, line.getRoute(), true);
			if (path != null)
				paths.add(path);
		}

		return paths.iterator();
	}


	public static Path shortestPath(LatLng start, LatLng end, Route route) {
		return shortestPathAux(start, end, route, false);
	}


	private static Path shortestPathAux(LatLng start, LatLng end, Route route, boolean checkGoodPath) {
		Path shortestPath = null;

		if (route.validStops()) {

			LatLng stopPoint;
			float distStartGo, distStartRet, distEndGo, distEndRet, busDist, totalWalked, travelDistScore, minTravelDistScore = 1e6f;
			Stop[] closestStopsStart, closestStopsEnd;

			closestStopsStart = route.getClosestStops(start);

			stopPoint = closestStopsStart[0].getLocation();
			distStartGo = walkingDistance(start, stopPoint);
			stopPoint = closestStopsStart[1].getLocation();
			distStartRet = walkingDistance(start, stopPoint);

			closestStopsEnd = route.getClosestStops(end);

			stopPoint = closestStopsEnd[0].getLocation();
			distEndGo = walkingDistance(stopPoint, end);
			stopPoint = closestStopsEnd[1].getLocation();
			distEndRet = walkingDistance(stopPoint, end);

			// Stop 1 Go, Stop 2 Go
			totalWalked = distStartGo + distEndGo;
			busDist = route.distanceBetweenStops(closestStopsStart[0], closestStopsEnd[0]);

			if (busDist != INVALID_DISTANCE) {
				minTravelDistScore = getDistanceScore(totalWalked, busDist);
				shortestPath = new Path(start, end, closestStopsStart[0], closestStopsEnd[0], totalWalked, busDist);
			}

			// Stop 1 Go, Stop 2 Ret
			totalWalked = distStartGo + distEndRet;
			busDist = route.distanceBetweenStops(closestStopsStart[0], closestStopsEnd[1]);

			if (busDist != INVALID_DISTANCE) {
				travelDistScore = getDistanceScore(totalWalked, busDist);
				if (travelDistScore < minTravelDistScore) {
					minTravelDistScore = travelDistScore;
					shortestPath = new Path(start, end, closestStopsStart[0], closestStopsEnd[1], totalWalked, busDist);
				}
			}

			// Stop 1 Ret, Stop 2 Go
			totalWalked = distStartRet + distEndGo;
			busDist = route.distanceBetweenStops(closestStopsStart[1], closestStopsEnd[0]);

			if (busDist != INVALID_DISTANCE) {
				travelDistScore = getDistanceScore(totalWalked, busDist);
				if (travelDistScore < minTravelDistScore) {
					minTravelDistScore = travelDistScore;
					shortestPath = new Path(start, end, closestStopsStart[1], closestStopsEnd[0], totalWalked, busDist);
				}
			}

			// Stop 1 Ret, Stop 2 Ret
			totalWalked = distStartRet + distEndRet;
			busDist = route.distanceBetweenStops(closestStopsStart[1], closestStopsEnd[1]);

			if (busDist != INVALID_DISTANCE) {
				travelDistScore = getDistanceScore(totalWalked, busDist);
				if (travelDistScore < minTravelDistScore)
					shortestPath = new Path(start, end, closestStopsStart[1], closestStopsEnd[1], totalWalked, busDist);
			}
		}

		if (shortestPath != null && checkGoodPath) {
			if (shortestPath.isGoodPath())
				return shortestPath;
			else
				return null;
		}

		return shortestPath;
	}


	public float getBusDistance() {
		return busDistance;
	}

	public float getWalkDistance() {
		return walkDistance;
	}

	// A value representing how good a given path is. Lower score is better
	private static float getDistanceScore(float walkingDistance, float busDistance) {
		return busDistance + walkingDistance * WALKING_DETERRENT;
	}

	private boolean isGoodPath() {
		float onlyWalkingDistance = walkingDistance(startLocation, endLocation);
		return walkDistance < onlyWalkingDistance &&
				walkDistance < busDistance;
	}

	@Override
	public int compareTo(@NonNull Path path) {
		return (int) (getDistanceScore(walkDistance, busDistance) - getDistanceScore(path.getWalkDistance(), path.getBusDistance()));
	}

	public LatLng getStartLocation() {
		return startLocation;
	}

	public LatLng getEndLocation() {
		return endLocation;
	}

	public Stop getFirstStop() {
		return firstStop;
	}

	public Stop getLastStop() {
		return lastStops;
	}
	
	public Line getLine() {
		return firstStop.getSection().getRoute().getLine();
	}

	public boolean isGo() {
		return firstStop.isGo;
	}

}
