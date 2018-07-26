package francotobias.tdpproyecto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
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

		LineFactory.initLines();

	/**
		TextView text = findViewById(R.id.texto);
		text.setMovementMethod(new ScrollingMovementMethod());
		text.setText(JunarHandler.requestLines().requestData());
	//**/
	}
}