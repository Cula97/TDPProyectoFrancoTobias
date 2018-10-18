package francotobias.tdpproyecto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private LatLng bahia;
	private EditText searchBox;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private MarkerOptions userMarker;
	private Location lastUserLocation;

	private boolean requestedPosition;
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

		searchBox = (EditText) findViewById(R.id.input_search);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		requestedPosition = false;
		userMarker = null;
		lastUserLocation = null;


		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if(requestedPosition) {
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
					requestedPosition = false;

					if(userMarker == null) {
						AssetManager assetManager = getAssets();
						Bitmap icon = null;
						try {
							InputStream istream = assetManager.open("location.png");
							icon = BitmapFactory.decodeStream(istream);
						} catch (IOException e) {
						}
						icon = Bitmap.createScaledBitmap(icon, 128, 128, false);

						userMarker = new MarkerOptions()
								.position(new LatLng(location.getLatitude(), location.getLongitude()))
								.icon(BitmapDescriptorFactory.fromBitmap(icon))
								.flat(true);
						mMap.addMarker(userMarker);
					}
					userMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
					lastUserLocation = location;
				}
			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {

			}

			@Override
			public void onProviderEnabled(String s) {

			}

			@Override
			public void onProviderDisabled(String s) {
				Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(i);
			}
		};
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
		bahia = new LatLng(-38.7171, -62.2655);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bahia, 14));
		init();
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

	// Very laggy on Snapdragons
	public void displayStops(Line line) {
		List<Stop> stops = line.getRoute().getStops();
		String asset;

		for (Stop s : stops) {
			asset = s.isGo ? "bus_stop_go.png" : "bus_stop_ret.png";
			mMap.addMarker(new MarkerOptions()
					.position(s.location)
					.icon(BitmapDescriptorFactory.fromAsset(asset)));
		}
	}


	private void init() {

		searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				if(i == EditorInfo.IME_ACTION_SEARCH || EditorInfo.IME_ACTION_DONE == i || KeyEvent.KEYCODE_ENTER == keyEvent.getAction() || KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
					Toast.makeText(getApplicationContext(), "Buscando "+searchBox.getText(), Toast.LENGTH_LONG).show();
					searchMap();
				}

				return false;
			}
		});

	}

	private void searchMap() {

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode){
			case 10:
				break;
			default:
				break;
		}
	}

	public void centerUser(View v) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{
						Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.INTERNET
				}, 10);
				return;
			}
		}
		requestedPosition = true;
		locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
	}

}
