package francotobias.tdpproyecto;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class LineFactory {
	private static final int ID_COLUMN;
	private static final int COORD_COLUMN;
	protected List<Line> lines;

	public void InitLines() {
		CSVWizard harry = JunarHandler.requestLines();
		CSVWizard herm;

		while (!harry.isFinished()) {
			String id = harry.columnValue(ID_COLUMN);
			Line l = new Line(id);

			// Creates both routes
			List<LatLng> routeGo = stringToCoords(harry.columnValue(COORD_COLUMN));
			harry.advanceRow();
			List<LatLng> routeReturn = stringToCoords(harry.columnValue(COORD_COLUMN));
			Route r = new Route(routeGo, routeReturn);

			l.setRoute(r);

			// Creates stops
			herm = JunarHandler.requestStops()





		}


	}




}
