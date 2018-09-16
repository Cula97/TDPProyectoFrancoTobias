package francotobias.tdpproyecto;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.SortedSet;
import java.util.TreeSet;

public class Path implements Comparable<Path>{
	private static final float MAX_WALKING_DISTANCE = 800;
	private static final float INVALID_DISTANCE = -1;

	private Stop firstStop;
	private Stop lastStops;
	private Line line;
	public final float distance;
	private boolean isGo;


	public Path(Stop firstStop, Stop lastStops, float distance) {
		this.firstStop = firstStop;
		this.lastStops = lastStops;
		this.distance = distance;
		line = firstStop.getSection().getRoute().getLine();
		isGo = firstStop.isGo;
	}


	public static Iterable<Path> shortestPaths(LatLng start, LatLng end) {
		SortedSet<Path> paths = new TreeSet<>();
		Path path;

		for (Line line : LineManager.lines()) {
			path = shortestPath(start, end, line);
			if (path != null)
				paths.add(path);
		}

		return paths;
	}


	public static Path shortestPath(LatLng start, LatLng end, Line line) {
		Path shortestPath = null;
		if (line.getRoute().validStops()) {

			Location startLocation = BusManager.latLngToLocation(start, null);
			Location endLocation = BusManager.latLngToLocation(end, null);
			Stop[] closestStopsStart, closestStopsEnd;
			Location stopLocation;
			float distStartGo, distStartRet, distEndGo, distEndRet, travelDist, minTravelDist = 1e5f;

			closestStopsStart = line.getClosestStops(startLocation);

			stopLocation = BusManager.latLngToLocation(closestStopsStart[0].location, null);
			distStartGo = startLocation.distanceTo(stopLocation);
			stopLocation = BusManager.latLngToLocation(closestStopsStart[1].location, null);
			distStartRet = startLocation.distanceTo(stopLocation);

			if (distStartGo <= MAX_WALKING_DISTANCE || distStartRet <= MAX_WALKING_DISTANCE) {
				closestStopsEnd = line.getClosestStops(endLocation);

				stopLocation = BusManager.latLngToLocation(closestStopsEnd[0].location, null);
				distEndGo = stopLocation.distanceTo(endLocation);
				stopLocation = BusManager.latLngToLocation(closestStopsEnd[1].location, null);
				distEndRet = stopLocation.distanceTo(endLocation);

				// Distance on the Go route to both closest stops
				if (distStartGo <= MAX_WALKING_DISTANCE) {

					if (distEndGo <= MAX_WALKING_DISTANCE) {
						travelDist = line.getRoute().distanceBetweenStops(closestStopsStart[0], closestStopsEnd[0]);
						if (travelDist != INVALID_DISTANCE) {
							minTravelDist = travelDist;
							shortestPath = new Path(closestStopsStart[0], closestStopsEnd[0], minTravelDist);
						}
					}

					if (distEndRet <= MAX_WALKING_DISTANCE) {
						travelDist = line.getRoute().distanceBetweenStops(closestStopsStart[0], closestStopsEnd[1]);
						if (travelDist != INVALID_DISTANCE && travelDist < minTravelDist) {
							minTravelDist = travelDist;
							shortestPath = new Path(closestStopsStart[0], closestStopsEnd[1], minTravelDist);
						}
					}
				}

				// Distance on the Ret route to both closest stops
				if (distStartRet <= MAX_WALKING_DISTANCE) {
					if (distEndGo <= MAX_WALKING_DISTANCE) {
						travelDist = line.getRoute().distanceBetweenStops(closestStopsStart[1], closestStopsEnd[0]);
						if (travelDist != INVALID_DISTANCE && travelDist < minTravelDist) {
							minTravelDist = travelDist;
							shortestPath = new Path(closestStopsStart[1], closestStopsEnd[0], minTravelDist);
						}
					}

					if (distEndRet <= MAX_WALKING_DISTANCE) {
						travelDist = line.getRoute().distanceBetweenStops(closestStopsStart[1], closestStopsEnd[1]);
						if (travelDist != INVALID_DISTANCE && travelDist < minTravelDist) {
							minTravelDist = travelDist;
							shortestPath = new Path(closestStopsStart[1], closestStopsEnd[1], minTravelDist);
						}
					}
				}
			}
		}

		return shortestPath;
	}


	@Override
	public int compareTo(@NonNull Path path) {
		return (int) (distance - path.distance);
	}

}
