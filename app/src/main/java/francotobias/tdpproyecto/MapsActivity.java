package francotobias.tdpproyecto;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsActivity extends AppCompatActivity
		implements
		OnMapReadyCallback,
		GoogleMap.OnMapClickListener,
		GoogleMap.OnMapLongClickListener,
		GoogleMap.OnMyLocationButtonClickListener {

	private GoogleMap mMap;
	private LatLng bahia;
	private LocationManager locationManager;
	private boolean permissionDenied;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

	private Path singlePath;
	private Iterable<Path> multiplePaths;
	private Marker start, end;

	private ViewGroup topBar;
	private boolean topBarAndHintVisible = true;
	private ViewGroup bottomBar;
	private boolean  bottomBarVisible = false;
	private TextView hintTextView;
	private float hintTextViewHeight;
	private ImageButton closeButton;
	private boolean selectingNewStartLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: no title, keep status (notification bar)
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_maps2);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapFragment);
		mapFragment.getMapAsync(this);

	}


	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		topBar = findViewById(R.id.topBarLayout);
		//TODO: bottomBar no tiene shadow
		// github.com/miguelhincapie/CustomBottomSheetBehavior/issues/32#issuecomment-275332696
		bottomBar = findViewById(R.id.bottomBarLayout);
		hintTextView = findViewById(R.id.textViewBottomBar);

		hintTextViewHeight = bottomBar.getPaddingTop()
								+ hintTextView.getHeight()
								+ hintTextView.getPaddingBottom();
		bottomBar.setY(bottomBar.getY() + bottomBar.getHeight() - hintTextViewHeight);

		bahia = new LatLng(-38.7171, -62.2655);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bahia, 14));

		mMap.setOnMapClickListener(this);
		mMap.setOnMapLongClickListener(this);
		mMap.setOnMyLocationButtonClickListener(this);
		mMap.getUiSettings().setMapToolbarEnabled(false);
		mMap.setPadding(0, topBar.getHeight(), 0, (int) hintTextViewHeight);
		enableMyLocation();

		ViewGroup mapView = findViewById(R.id.mainLayout);
		View locationButton = mapView.findViewWithTag("GoogleMapMyLocationButton");
		RelativeLayout.LayoutParams buttonLayout = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
		buttonLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
		buttonLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		buttonLayout.bottomMargin = buttonLayout.rightMargin;

		//TODO: alinear los botones de la manera correcta y poco mas

		closeButton = findViewById(R.id.closePathButton);
		RelativeLayout.LayoutParams closeButtonLayout = (RelativeLayout.LayoutParams) closeButton.getLayoutParams();
		closeButtonLayout.addRule(RelativeLayout.ALIGN_END, locationButton.getId());

	}


	public void displayRoute(Line line) {
		List<LatLng> routeGo = line.getRoute().getGo();
		List<LatLng> routeRet = line.getRoute().getReturn();

		mMap.addPolyline(new PolylineOptions()
				.addAll(routeGo)
				.zIndex(1)
				.color(Color.BLUE));

		mMap.addPolyline(new PolylineOptions()
				.addAll(routeRet)
				.zIndex(1)
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


	private void enableMyLocation() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// Permission to access the location is missing.
			PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
					Manifest.permission.ACCESS_FINE_LOCATION, true);
		} else if (mMap != null) {
			// Access to the location has been granted to the app.
			mMap.setMyLocationEnabled(true);
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
	}


	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
			return;
		}

		if (PermissionUtils.isPermissionGranted(permissions, grantResults,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
			// Enable the my location layer if the permission has been granted.
			enableMyLocation();
		} else {
			// Display the missing permission error dialog when the fragments resume.
			permissionDenied = true;
		}
	}


	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (permissionDenied) {
			// Permission was not granted, display error dialog.
			showMissingPermissionError();
			permissionDenied = false;
		}
	}


	// Displays a dialog with error message explaining that the location permission is missing.
	private void showMissingPermissionError() {
		PermissionUtils.PermissionDeniedDialog
				.newInstance(true).show(getSupportFragmentManager(), "dialog");
	}


	@Override
	public boolean onMyLocationButtonClick() {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getApplicationContext(), R.string.gpsDisabled, Toast.LENGTH_SHORT).show();
			return true;
		}

		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}


	@Override
	public void onMapClick(LatLng point) {

		if (selectingNewStartLocation) {
			if (bottomBarVisible)
				hideBottomAndTopBar();
			else
				showBottomAndTopBar();

		}
		else
			if (!bottomBarVisible)
				if (!topBarAndHintVisible)
					showTopBarAndHint();
				else
					hideTopBarAndHint();
			else
				hideBottomBar();
	}


	@Override
	public void onMapLongClick (LatLng point) {

		if (selectingNewStartLocation) {
			if (start != null)
				start.remove();

			start = mMap.addMarker(new MarkerOptions()
					.position(point)
					.title(getResources().getString(R.string.startTrip)));

		}
		else {
			showBottomAndTopBar();

			if (end != null)
				end.remove();

			end = mMap.addMarker(new MarkerOptions()
					.position(point)
					.title(getResources().getString(R.string.endTrip)));
		}
	}


	private void showTopBarAndHint() {
		if (!topBarAndHintVisible) {
			topBar.animate().translationYBy((float) topBar.getHeight());
			bottomBar.animate().translationYBy(-hintTextViewHeight);
			//TODO: sincronizar con el final de la animacion si es podible
			mMap.setPadding(0, topBar.getHeight(), 0, (int) hintTextViewHeight);
			topBarAndHintVisible = true;
		}
	}


	private void hideTopBarAndHint() {
		if (topBarAndHintVisible) {
			topBar.animate().translationYBy((float) -topBar.getHeight());
			bottomBar.animate().translationYBy(hintTextViewHeight);
			mMap.setPadding(0, 0, 0, 0);
			topBarAndHintVisible = false;
		}
	}


	private void showBottomAndTopBar() {
		if (!bottomBarVisible) {
			float amount = bottomBar.getHeight();

			if (!topBarAndHintVisible) {
				topBar.animate().translationYBy((float) topBar.getHeight());
				topBarAndHintVisible = true;
			}
			else
				if (!selectingNewStartLocation)
					amount -= hintTextViewHeight;

			//TODO: animar transicion de texto
			hintTextView.setText(R.string.addStartLocation);
			bottomBar.animate().translationYBy(-amount);
			mMap.setPadding(0, topBar.getHeight(), 0, bottomBar.getHeight());

			bottomBarVisible = true;
		}
	}

	private void hideBottomAndTopBar() {
		if (bottomBarVisible) {
			float amount = bottomBar.getHeight();

			if (topBarAndHintVisible) {
				topBar.animate().translationYBy( - (float) topBar.getHeight());
				topBarAndHintVisible = false;
			}

			hintTextView.setText(R.string.addStartLocation);
			bottomBar.animate().translationYBy(amount);
			mMap.setPadding(0, 0, 0, 0);

			bottomBarVisible = false;
		}
	}

	private void hideBottomBar() {
		if (bottomBarVisible) {
			hintTextView.setText(R.string.addEndLocation);
			bottomBar.animate().translationYBy(bottomBar.getHeight() - hintTextViewHeight);
			mMap.setPadding(0, topBar.getHeight(), 0, (int) hintTextViewHeight);
			bottomBarVisible = false;
		}
	}


	public void onCurrentLocationClick(View view) {
		if (onMyLocationButtonClick())
			return;
		try {
			Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			//TODO: wait for location if GPS was JUST enabled
			if (currentLocation == null) {
				Toast.makeText(getApplicationContext(), R.string.unableRetriveGps, Toast.LENGTH_SHORT).show();
				return;
			}

		/*  Manija y anda mal
			if (currentLocation == null) {
				Toast.makeText(getApplicationContext(), R.string.waitGps, Toast.LENGTH_SHORT).show();
				Thread.sleep(5000);
				currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (currentLocation == null) {
					Toast.makeText(getApplicationContext(), R.string.unableRetriveGps, Toast.LENGTH_SHORT).show();
					return;
				}
			}
		*/


			if (start != null)
				start.remove();

			start = mMap.addMarker(new MarkerOptions()
							.position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
							.title(getResources().getString(R.string.startTrip)));

		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
	}


	public void onNewLocationClick(View view) {
		selectingNewStartLocation = true;
		hintTextView.setVisibility(View.GONE);
	}

	public void onCloseClick(View view) {

	}
}
