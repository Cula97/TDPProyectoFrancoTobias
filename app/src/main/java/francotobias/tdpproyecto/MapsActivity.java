package francotobias.tdpproyecto;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private LatLng bahia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
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
		bahia =  new LatLng(-38.7171, -62.2655);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bahia, 14));

		/**
		//Testeando marcadores
		AssetManager assetManager = getAssets();
		Bitmap bitmap = null;
		try {
			InputStream istream = assetManager.open("bus_stop.png");
			bitmap = BitmapFactory.decodeStream(istream);
		} catch (IOException e) {}
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false);
		mMap.addMarker(new MarkerOptions()
				.position(bahia)
		//		.icon(BitmapDescriptorFactory.fromAsset(getResources().getString(R.string.bus_stop_icon))));
				.icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));
		/**
		//Testeando recorridos
		LatLng l1 = new LatLng( -38.688052,-62.314912);
		LatLng l2 = new LatLng( -38.684719, -62.31069);
		LatLng l3 = new LatLng( -38.68329, -62.308978);
		LatLng l4 = new LatLng( -38.701691, -62.284786);
		LinkedList<LatLng> routeGo= new LinkedList<>();
		routeGo.add(l1);
		routeGo.add(l2);
		routeGo.add(l3);
		routeGo.add(l4);
		mMap.addPolyline(new PolylineOptions()
				.addAll(routeGo)
				.color(Color.BLUE)
				.width(10));
		 **/
	}

	public void displayRoute(Line line) {
		List<LatLng> routeGo = line.getRoute().getGo();
		List<LatLng> routeRet = line.getRoute().getReturn();
		mMap.addPolyline(new PolylineOptions()
				.addAll(routeGo)
				.color(Color.BLUE));
		mMap.addPolyline(new PolylineOptions()
				.addAll(routeRet)
				.color(Color.RED));
	}

	public void displayStops(Line line) {
		List<Stop> stops = line.getRoute().getStops();

		AssetManager assetManager = getAssets();
		Bitmap icon = null;
		try {
			InputStream istream = assetManager.open("bus_stop.png");
			icon = BitmapFactory.decodeStream(istream);
		} catch (IOException e) {}
		Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);

		for (Stop s : stops)
			mMap.addMarker(new MarkerOptions()
					.position(bahia)
					.icon(BitmapDescriptorFactory.fromBitmap(scaledIcon)));

	}

}
