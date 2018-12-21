package francotobias.tdpproyecto.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

import francotobias.tdpproyecto.R;

public class DataManager {
	//Nota: debe ser una clase estática.
	private static String UPDATE_FILENAME = "config.cfg";
	private static String LASTDATE_KEY = "last_update";
	private static String FIRST_TIME_KEY = "FirstTime";

	private static String ROUTES_FILENAME = "RouteFile";
	private static String LINES_FILENAME = "LinesFile";
	private static String STOPSGO_FILENAME = "StopsGoFile";
	private static String STOPSRET_FILENAME = "StopsRetFile";
	private static String STOPS_FILENAME = "StopsFile";
	private static long ONE_WEEK = 604800000;
	//private static long ONE_WEEK = 6000000;
	private static DataManager singleObject = null;
	private Context context;

	private DataManager() {
	}

	public static DataManager getInstance() {
		if (singleObject == null)
			singleObject = new DataManager();

		return singleObject;
	}

	public void startUpdater(Context c) {
		context = c;
	}


	private void forceUpdate() {
		SharedPreferences sp = context.getSharedPreferences(UPDATE_FILENAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		CSVWizard updatedData, CSVlines;
		FileHandler file;

		// UPDATE ROUTES
		updatedData = JunarHandler.requestRoutes();
		file = new FileHandler(context, ROUTES_FILENAME);
		file.writeFileData(updatedData.requestData());


		// UPDATE LINES
		updatedData = JunarHandler.requestLines();
		file = new FileHandler(context, LINES_FILENAME);
		file.writeFileData(updatedData.requestData());

		// UPDATE ALL LINES
		CSVlines = updatedData;
		CSVlines.restart();
		while (!CSVlines.isFinished()) {
			String id = CSVlines.columnValue(1);
			//UPDATE SPECIFIC LINE

			// GO
			file = new FileHandler(context, STOPSGO_FILENAME + id);
			updatedData = JunarHandler.requestStopsGo(id);
			file.writeFileData(updatedData.requestData());

			// RET
			file = new FileHandler(context, STOPSRET_FILENAME + id);
			updatedData = JunarHandler.requestStopsRet(id);
			file.writeFileData(updatedData.requestData());

			CSVlines.advanceRow();
		}

		editor.putLong(LASTDATE_KEY, Calendar.getInstance().getTimeInMillis());
		editor.apply();
	}

	public boolean needUpdate() {
		SharedPreferences sp = context.getSharedPreferences(UPDATE_FILENAME, Context.MODE_PRIVATE);
		boolean res = false;

		if (Calendar.getInstance().getTimeInMillis() - sp.getLong(LASTDATE_KEY, 0) > ONE_WEEK)
			res = true;

		return res;
	}

	public void update() {
		if (needUpdate())
			forceUpdate();
	}

	public CSVWizard requestStopsGo(String line) {
		if (line.equals("513"))
			return new CSVWizard(context.getResources().getString(R.string.i513));

		FileHandler file = new FileHandler(context, STOPSGO_FILENAME + line);
		return new CSVWizard(file.requestFileData());
	}

	public CSVWizard requestStopsRet(String line) {
		if (line.equals("513"))
			return new CSVWizard(context.getResources().getString(R.string.v513));

		FileHandler file = new FileHandler(context, STOPSRET_FILENAME + line);
		return new CSVWizard(file.requestFileData());
	}

	public CSVWizard requestRoutes() {
		FileHandler file = new FileHandler(context, ROUTES_FILENAME);
		return new CSVWizard(file.requestFileData());

	}

	public CSVWizard requestLines() {
		FileHandler file = new FileHandler(context, LINES_FILENAME);
		return new CSVWizard(file.requestFileData());
	}

}
