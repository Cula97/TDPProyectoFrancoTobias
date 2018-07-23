package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

public class LineFactory {
	private static final int ID_COLUMN = 1;
	private static final int COORD_COLUMN = 3;
	private static final int LAT_COLUMN = 3;
	private static final int LNG_COLUMN = 4;
	private static final int DIRECTION_COLUMN = 2;

	protected static List<Line> lines = new LinkedList<>();

	public static void InitLines() {
		CSVWizard CSVlines = JunarHandler.requestLines();
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
			CSVstops = JunarHandler.requestStops(id);
			List<Stop> s = new LinkedList<>();
			r.setStops(s);
			while (!CSVstops.isFinished()) {
				Double lat = Double.parseDouble(CSVstops.columnValue(LAT_COLUMN));
				Double lng = Double.parseDouble(CSVstops.columnValue(LNG_COLUMN));
				Character direction = CSVlines.columnValue(DIRECTION_COLUMN).charAt(0);
				s.add(new Stop(lat, lng, direction == 'i'));

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
			Double lat = Double.parseDouble(latsLngs[0]);
			Double lng = Double.parseDouble(latsLngs[1]);
			toReturn.add(new LatLng(lat, lng));
		}

		return toReturn;

	}


}
