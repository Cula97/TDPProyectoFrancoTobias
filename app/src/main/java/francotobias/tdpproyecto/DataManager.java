package francotobias.tdpproyecto;

import android.content.Context;
import android.content.SharedPreferences;

public class DataManager {
	//Nota: debe ser una clase estática.
	private static String UPDATE_FILENAME = "UpdateInformation";
	private static String LASTDATE_KEY = "LastDate";
	private static String FIRST_TIME_KEY = "FirstTime";

	private static String ROUTES_FILENAME = "RouteFile";
	private static String LINES_FILENAME = "LinesFile";
	private static String STOPSGO_FILENAME = "StopsGoFile";
	private static String STOPSRET_FILENAME = "StopsRetFile";
	private static String STOPS_FILENAME = "StopsFile";

	private Context context;

	private static DataManager singleObject = null;

	private DataManager(){
	}

	public static DataManager getInstance(){
		if(singleObject == null)
			singleObject = new DataManager();

		return singleObject;
	}

	public void startUpdater(Context c){ context = c;  }


	public void forceUpdate() {
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

	}

	public boolean needUpdate() {
		boolean FT = false;
		SharedPreferences sp = context.getSharedPreferences("UpdateInformation", 0);
		if (sp.getBoolean(FIRST_TIME_KEY, true)) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(FIRST_TIME_KEY, false);
			editor.commit();
			FT = true;
		}

		return FT;
	}

	public void update() {
		if (needUpdate())
			forceUpdate();
	}


	public CSVWizard requestStopsGo(String line) {
		FileHandler file = new FileHandler(context, STOPSGO_FILENAME + line);
		return new CSVWizard(file.requestFileData());
	}

	public CSVWizard requestStopsRet(String line) {
		FileHandler file = new FileHandler(context, STOPSRET_FILENAME + line);
		return new CSVWizard(file.requestFileData());
	}

	// No es guardado
	public CSVWizard requestGPS(String line) {
		return JunarHandler.requestGPS(line);
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
