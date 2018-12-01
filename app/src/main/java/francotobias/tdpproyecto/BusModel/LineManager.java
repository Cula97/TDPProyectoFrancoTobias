package francotobias.tdpproyecto.BusModel;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import francotobias.tdpproyecto.Helpers.CSVWizard;
import francotobias.tdpproyecto.Helpers.DataManager;
import francotobias.tdpproyecto.PathModel.Route;
import francotobias.tdpproyecto.PathModel.Stop;

public class LineManager {
	private static final int ID_COLUMN = 1;
	private static final int COORD_COLUMN = 3;
	private static final int LAT_COLUMN = 3;
	private static final int LNG_COLUMN = 4;

	private static SortedMap<String, Line> lines = new TreeMap<>();

	public static void initLines() {
		CSVWizard CSVlines = DataManager.getInstance().requestLines();
		CSVWizard CSVstops;


		while (!CSVlines.isFinished()) {
			String id = CSVlines.columnValue(ID_COLUMN);
			Line line = new Line(id);
			lines.put(id, line);

			// Creates both routes
			List<LatLng> routeGo = stringToCoords(CSVlines.columnValue(COORD_COLUMN));
			CSVlines.advanceRow();
			List<LatLng> routeReturn = stringToCoords(CSVlines.columnValue(COORD_COLUMN));
			Route route = new Route(line, routeGo, routeReturn);

			// Creates stops
			CSVstops = DataManager.getInstance().requestStopsGo(id);
			List<Stop> stops = new LinkedList<>();

			while (!CSVstops.isFinished()) {
				Double lat = Double.parseDouble(CSVstops.columnValue(LAT_COLUMN));
				Double lng = Double.parseDouble(CSVstops.columnValue(LNG_COLUMN));
				stops.add(new Stop(lat, lng, true));

				CSVstops.advanceRow();
			}

			CSVstops = DataManager.getInstance().requestStopsRet(id);
			while (!CSVstops.isFinished()) {
				Double lat = Double.parseDouble(CSVstops.columnValue(LAT_COLUMN));
				Double lng = Double.parseDouble(CSVstops.columnValue(LNG_COLUMN));
				stops.add(new Stop(lat, lng, false));

				CSVstops.advanceRow();
			}

			route.setStops(stops);
			CSVlines.advanceRow();
		}

	}

	protected static List<LatLng> stringToCoords(String s) {
		List<LatLng> toReturn = new LinkedList<>();
		String[] coords = s.split(",0 ");

		for (int i = 0; i < coords.length; i++) {
			String latsLngs[] = coords[i].split(",");
			//El archivo contiene Lng Lat
			Double lat = Double.parseDouble(latsLngs[1]);
			Double lng = Double.parseDouble(latsLngs[0]);
			toReturn.add(new LatLng(lat, lng));
		}

		return toReturn;

	}

	public static Line getLine(String ID) {
		Line line = lines.get(ID);

		if (line == null)
			Log.d("Ruta no existe", ID);

		return line;
	}

	public static Iterable<Line> lines() {
		return lines.values();
	}

}
