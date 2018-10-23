package francotobias.tdpproyecto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import francotobias.tdpproyecto.DataVisualizer.VisualizeDataMapActivity;

public class MainActivity extends AppCompatActivity {
	public static final String DEBUG_LINE = "francotobias.tdpproyecto.DEBUG_LINE";
	public static final String DEBUG_MODE = "francotobias.tdpproyecto.DEBUG_MODE";

	// La actividad de entrada a la aplicacion es MapsActivity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button MapButton = findViewById(R.id.buttonMap);
		MapButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), MapsActivity.class);
				startActivity(intent);
			}
		});

		DataManager manager = DataManager.getInstance();
		manager.startUpdater(this);
		manager.update();

		LineManager.initLines();

		TextView text = findViewById(R.id.texto);
		//text.setMovementMethod(new ScrollingMovementMethod());
		FileHandler file1 = new FileHandler(this, "Data");
		file1.writeFileData("Hola 123 \n Holas 12345 \n Hoals 12345");
		FileHandler file2 = new FileHandler(this, "Data");
		text.setText(DataManager.getInstance().requestGPS("513").requestData());

		Spinner spinner = findViewById(R.id.spinnerMode);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.debug_modes_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		spinner.setSelection(0);


		ArrayList<String> lines = new ArrayList<>();
		for (Line line : LineManager.lines())
			if (line.getRoute().validStops())
				lines.add(line.lineID);
		lines.add("-----");
		for (Line line : LineManager.lines())
			if (!line.getRoute().validStops() && line.getRoute().getStops() != null)
				lines.add(line.lineID);

		Spinner lineSpinner = findViewById(R.id.spinnerLines);
		ArrayAdapter<String> lineSpinnerAdapter = new ArrayAdapter<>
				(this, android.R.layout.simple_spinner_item, lines);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lineSpinner.setAdapter(lineSpinnerAdapter);

		lineSpinner.setSelection(lines.indexOf("504"));
	}


	public void launchVisulizeDataActivity(View view) {
		Intent intent = new Intent(this, VisualizeDataMapActivity.class);
		Spinner lineSpinner = findViewById(R.id.spinnerLines);
		String line = lineSpinner.getSelectedItem().toString();

		intent.putExtra(DEBUG_LINE, line);

		Spinner modeSpinner = findViewById(R.id.spinnerMode);
		String mode = modeSpinner.getSelectedItem().toString();
		intent.putExtra(DEBUG_MODE, mode);

		startActivity(intent);
	}
}