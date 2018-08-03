package francotobias.tdpproyecto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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

//		LineFactory.initLines();

		TextView text = findViewById(R.id.texto);
		//text.setMovementMethod(new ScrollingMovementMethod());
		FileHandler file1 = new FileHandler(this, "Data");
		file1.writeFileData("Hola 123 \n Holas 12345 \n Hoals 12345");
		FileHandler file2 = new FileHandler(this, "Data");
		text.setText(file2.requestFileData());

	}
}