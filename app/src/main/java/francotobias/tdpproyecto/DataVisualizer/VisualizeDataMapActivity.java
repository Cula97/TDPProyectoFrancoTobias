package francotobias.tdpproyecto.DataVisualizer;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import francotobias.tdpproyecto.Line;
import francotobias.tdpproyecto.LineManager;
import francotobias.tdpproyecto.MainActivity;
import francotobias.tdpproyecto.R;
import francotobias.tdpproyecto.Stop;

public class VisualizeDataMapActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private Line line;
	private int goSectionIndex;
	private int goSectionAmount;
	private int retSectionIndex;
	private int retSectionAmount;
	private int stopIndex;
	private int stopAmount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visualize_data_map);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		line = LineManager.getLine(getIntent().getStringExtra(MainActivity.DEBUG_LINE));
		goSectionIndex = 0;
		retSectionIndex = 0;
		goSectionAmount = line.getRoute().getGo().size();
		retSectionAmount = line.getRoute().getReturn().size();
		stopIndex = 0;
		stopAmount = line.getRoute().getStops().size();
	}


	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-38.7171, -62.2655), 14));
	}

	public void displaySection(View view) {
		List<LatLng> routeGo = line.getRoute().getGo();
		List<LatLng> routeRet = line.getRoute().getReturn();

		if (goSectionIndex < goSectionAmount - 1)
			mMap.addPolyline(new PolylineOptions()
					.add(routeGo.get(goSectionIndex), routeGo.get(++goSectionIndex))
					.color(Color.BLUE));
		else
			if (retSectionIndex < retSectionAmount - 1)
				mMap.addPolyline(new PolylineOptions()
						.add(routeRet.get(retSectionIndex), routeRet.get(++retSectionIndex))
						.color(Color.RED));
			else
				Toast.makeText(getApplicationContext(), "Recorrido finalizado", Toast.LENGTH_SHORT).show();
	}

	public void displayStop(View view) {
		if (stopIndex < stopAmount) {
			List<Stop> stops = line.getRoute().getStops();

			String type = "_ret";

			if (stops.get(stopIndex).isGo())
				type = "_go";

			AssetManager assetManager = getAssets();
			Bitmap icon = null;
			try {
				InputStream istream = assetManager.open("bus_stop"+ type +".png");
				icon = BitmapFactory.decodeStream(istream);
			} catch (IOException e) {}

			Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);

			mMap.addMarker(new MarkerOptions()
					.position(stops.get(stopIndex).getLocation())
					.icon(BitmapDescriptorFactory.fromBitmap(scaledIcon))
					.flat(true));

			stopIndex += 3; // Demasiadas paradas en pantalla
		}
		else
			Toast.makeText(getApplicationContext(), "No hay más paradas", Toast.LENGTH_SHORT).show();
	}


}
