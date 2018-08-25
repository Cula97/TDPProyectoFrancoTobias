package francotobias.tdpproyecto;

import android.location.Location;
import android.util.Log;

import java.util.Calendar;
import java.util.Map;

public class BusManager {
	private static final int ID_COLUMN = 2;
	private static final int LAT_COLUMN = 5;
	private static final int LNG_COLUMN = 6;
	private static final int VEL_COLUMN = 7;
	private static final int ANGLE_COLUMN = 8;
	private static final String PROVIDER_NAME = "API JUNAR";


	public static void addBuses(Line line) {
		CSVWizard CSVgps = DataManager.requestGPS(line.getID());

		while (!CSVgps.isFinished()) {
			String id = CSVgps.columnValue(ID_COLUMN);
			double lat = Double.parseDouble(CSVgps.columnValue(LAT_COLUMN));
			double lng = Double.parseDouble(CSVgps.columnValue(LNG_COLUMN));
			int vel = Integer.parseInt(CSVgps.columnValue(VEL_COLUMN));
			int ang = Integer.parseInt(CSVgps.columnValue(ANGLE_COLUMN));

		/**
			Log.d("Leido", line.lineID + " " + id + " " +
					Double.toString(lat) + " " + Double.toString(lng) + " " +
					ang + " " + vel);
		**/

			Location loc = new Location(PROVIDER_NAME);
			loc.setLatitude(lat);
			loc.setLongitude(lng);
			loc.setSpeed(vel);
			loc.setBearing(ang);
			loc.setTime(Calendar.getInstance().getTimeInMillis());

			line.addBus(new Bus(id, line, loc));

			CSVgps.advanceRow();
		}
	}

	public static void updateBuses(Line line) {
		CSVWizard CSVgps = DataManager.requestGPS(line.getID());
		Map<String, Bus> savedFleet = line.getFleet();
		Bus bus;

		while (!CSVgps.isFinished()) {
			String id = CSVgps.columnValue(ID_COLUMN);
			double lat = Double.parseDouble(CSVgps.columnValue(LAT_COLUMN));
			double lng = Double.parseDouble(CSVgps.columnValue(LNG_COLUMN));
			int vel = Integer.parseInt(CSVgps.columnValue(VEL_COLUMN));
			int ang = Integer.parseInt(CSVgps.columnValue(ANGLE_COLUMN));

			if (savedFleet.containsKey(id)) {
				bus = savedFleet.get(id);
				Location busLoc = bus.getLocation();

				busLoc.setLatitude(lat);
				busLoc.setLongitude(lng);
				busLoc.setSpeed(vel);
				busLoc.setBearing(ang);
				busLoc.setTime(Calendar.getInstance().getTimeInMillis());
			} else {
				Location loc = new Location(PROVIDER_NAME);
				loc.setLatitude(lat);
				loc.setLongitude(lng);
				loc.setSpeed(vel);
				loc.setBearing(ang);
				loc.setTime(Calendar.getInstance().getTimeInMillis());

				line.addBus(new Bus(id, line, loc));
			}

			CSVgps.advanceRow();
		}
	}

}
