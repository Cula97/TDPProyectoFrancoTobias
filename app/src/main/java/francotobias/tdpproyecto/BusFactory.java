package francotobias.tdpproyecto;

import android.location.Location;

import java.util.Date;

public class BusFactory {
	private static final int ID_COLUMN = 2;
	private static final int LAT_COLUMN = 5;
	private static final int LNG_COLUMN = 6;
	private static final int VEL_COLUMN = 7;
	private static final int ANGLE_COLUMN = 8;
	private static final String PROVIDER_NAME = "API JUNAR";


	public static void addBuses(Line line) {
		CSVWizard CSVgps = JunarHandler.requestGPS(line.getID());

		while (!CSVgps.isFinished()) {
			String id = CSVgps.columnValue(ID_COLUMN);
			double lat = Double.parseDouble(CSVgps.columnValue(LAT_COLUMN));
			double lng = Double.parseDouble(CSVgps.columnValue(LNG_COLUMN));
			int vel = Integer.parseInt(CSVgps.columnValue(VEL_COLUMN));
			int ang = Integer.parseInt(CSVgps.columnValue(ANGLE_COLUMN));

			Location loc = new Location(PROVIDER_NAME);
			loc.setLatitude(lat);
			loc.setLongitude(lng);
			loc.setSpeed(vel);
			loc.setBearing(ang);
			loc.setTime(new Date().getTime());

			line.addBus(new Bus(id, line, loc));
		}



	}


}
