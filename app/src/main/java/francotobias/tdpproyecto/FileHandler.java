package francotobias.tdpproyecto;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileHandler {
	private Context context;
	private String FILE_NAME;
	private StringBuffer buffer = null;

	public FileHandler(Context context, String filename) {
		this.context = context;
		FILE_NAME = filename;
	}

	public String requestFileData() {
		if (buffer == null) {
			try {
				buffer = new StringBuffer();

				BufferedReader inputReader = new BufferedReader(new InputStreamReader(context.openFileInput(FILE_NAME + ".csv")));
				String input;

				while ((input = inputReader.readLine()) != null)
					buffer.append(input).append('\n');

				inputReader.close();
			} catch (IOException e) {
			}
		}

		return buffer.toString();
	}

	public void writeFileData(String data) {

		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.getApplicationContext().openFileOutput(FILE_NAME + ".csv", Context.MODE_PRIVATE));
			outputStreamWriter.flush();
			outputStreamWriter.write(data);
			outputStreamWriter.close();
			buffer = new StringBuffer();
			buffer.append(data);
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}

	}
}
