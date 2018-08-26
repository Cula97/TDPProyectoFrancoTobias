package francotobias.tdpproyecto;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

public class LineManager {
	private static final int ID_COLUMN = 1;
	private static final int COORD_COLUMN = 3;
	private static final int LAT_COLUMN = 3;
	private static final int LNG_COLUMN = 4;

	// TODO: Reemplazar por un Map<lineID, ID> maybe?
	protected static List<Line> lines = new LinkedList<>();

	public static void initLines() {
		CSVWizard CSVlines = DataManager.requestLines();
		CSVWizard CSVstops;


		while (!CSVlines.isFinished()) {
			String id = CSVlines.columnValue(ID_COLUMN);
			Line l = new Line(id);
			lines.add(l);

			// Creates both routes
			List<LatLng> routeGo = stringToCoords(CSVlines.columnValue(COORD_COLUMN));
			CSVlines.advanceRow();
			List<LatLng> routeReturn = stringToCoords(CSVlines.columnValue(COORD_COLUMN));
			Route r = new Route(l, routeGo, routeReturn);

			// Creates stops
			CSVstops = DataManager.requestStopsGo(id);
			List<Stop> s = new LinkedList<>();
			r.setStops(s);
			while (!CSVstops.isFinished()) {
				Double lat = Double.parseDouble(CSVstops.columnValue(LAT_COLUMN));
				Double lng = Double.parseDouble(CSVstops.columnValue(LNG_COLUMN));
				s.add(new Stop(lat, lng, true));

				CSVstops.advanceRow();
			}

			CSVstops = DataManager.requestStopsRet(id);
			while (!CSVstops.isFinished()) {
				Double lat = Double.parseDouble(CSVstops.columnValue(LAT_COLUMN));
				Double lng = Double.parseDouble(CSVstops.columnValue(LNG_COLUMN));
				s.add(new Stop(lat, lng, false));

				CSVstops.advanceRow();
			}

			CSVlines.advanceRow();
		}

	}

	protected static List<LatLng> stringToCoords(String s) {
		List<LatLng> toReturn = new LinkedList<>();
		String[] coords = s.split(",0 ");

		for (int i = 0; i < coords.length; i++) {
			String latsLngs[] = coords[i].split(",");
			//El archivo contiene Long Lat
			Double lat = Double.parseDouble(latsLngs[1]);
			Double lng = Double.parseDouble(latsLngs[0]);
			toReturn.add(new LatLng(lat, lng));
		}

		return toReturn;

	}

	public static Line getLine(String ID) {
		for (Line l : lines)
			if (ID.equals(l.getID()))
				return l;

		Log.d("Ruta no encontrada", ID + " no existe");
		return null;
	}

}
