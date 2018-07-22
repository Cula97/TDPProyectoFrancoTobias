package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class LineFactory {
	private static final int ID_COLUMN;
	private static final int COORD_COLUMN;
	protected List<Line> lines;

	public void InitLines() {
		CSVWizard CSVlines = JunarHandler.requestLines();
		CSVWizard CSVstops;

		while (!CSVlines.isFinished()) {
			String id = CSVlines.columnValue(ID_COLUMN);
			Line l = new Line(id);

			// Creates both routes
			List<LatLng> routeGo = stringToCoords(CSVlines.columnValue(COORD_COLUMN));
			CSVlines.advanceRow();
			List<LatLng> routeReturn = stringToCoords(CSVlines.columnValue(COORD_COLUMN));
			Route r = new Route(routeGo, routeReturn);

			l.setRoute(r);

			// Creates stops
			CSVstops = JunarHandler.requestStops(id);
			while (!CSVstops.isFinished()) {

			}





		}


	}




}
