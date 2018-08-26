package francotobias.tdpproyecto;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

		askLocationPermission();

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

	}

	public void launchVisulizeDataActivit(View view) {
		Intent intent = new Intent(this, VisualizeDataMapActivity.class);
		EditText editText = findViewById(R.id.editTextDebugLine);
		String line = editText.getText().toString();
		if (line.equals(""))
			line = "502";

		intent.putExtra(DEBUG_LINE, line);

		Spinner spinner = findViewById(R.id.spinnerMode);
		String mode = spinner.getSelectedItem().toString();
		intent.putExtra(DEBUG_MODE, mode);

		startActivity(intent);
	}









	public void askLocationPermission() {
		if(ContextCompat.checkSelfPermission(MainActivity.this,
				Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			return;
		}
		else {
			requestLocationPermission();
		}
	}

	public void requestLocationPermission(){
		if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

			new AlertDialog.Builder(this)
					.setTitle("Permission needed")
					.setMessage("The app need this permission to be able to work properly.")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.create().show();

		} else {
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 1)  {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			} else {
				Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
			}
		}
	}














}