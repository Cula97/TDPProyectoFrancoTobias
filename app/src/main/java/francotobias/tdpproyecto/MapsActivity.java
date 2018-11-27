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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import francotobias.tdpproyecto.BusModel.Line;
import francotobias.tdpproyecto.BusModel.LineManager;
import francotobias.tdpproyecto.Helpers.PermissionUtils;
import francotobias.tdpproyecto.PathModel.Path;

public class MapsActivity extends AppCompatActivity
		implements
		OnMapReadyCallback,
		GoogleMap.OnMapClickListener,
		GoogleMap.OnMyLocationClickListener {

	private GoogleMap mMap;
	private LocationManager locationManager;
	private boolean permissionDenied;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

	private Path singlePath;
	private Iterator<Path> multiplePaths;
	private Marker start, end;

	private ViewGroup topBar;
	private ViewGroup sideBar;
	private boolean topBarAndSideBarVisible = true;
	private boolean addingStartMarker = false;
	private boolean addingDestinationMarker = false;

	private ImageButton exitButton;
	private Spinner lineSpinner;
	private static ArrayList<String> lineIDs;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: no title, keep status (notification bar)
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_maps);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapFragment);
		mapFragment.getMapAsync(this);

		// Populate Spinner
		lineSpinner = findViewById(R.id.lineSpinner);
		lineIDs = new ArrayList<>();
		lineIDs.add(getString(R.string.any));
		for (Line l : LineManager.lines())
			lineIDs.add(l.lineID);

		ArrayAdapter<String> lineSpinnerAdapter = new ArrayAdapter<>
				(this, android.R.layout.simple_spinner_item, lineIDs);
		lineSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lineSpinner.setAdapter(lineSpinnerAdapter);

		lineSpinner.setSelection(0);
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
		sideBar= findViewById(R.id.conrtolsLinearLayout);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-38.7171, -62.2655), 14));

		mMap.setOnMapClickListener(this);
		mMap.setOnMyLocationClickListener(this);

		mMap.getUiSettings().setMapToolbarEnabled(false);
		mMap.getUiSettings().setMyLocationButtonEnabled(false);
		enableMyLocation();

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


	public void onCurrentLocationButtonClick(View view) {
		// Check for enabled GPS
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getApplicationContext(), R.string.gpsDisabled, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check for valid location
		Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (currentLocation == null) {
			Toast.makeText(getApplicationContext(), R.string.unableRetriveGps, Toast.LENGTH_SHORT).show();
			return;
		}

		LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
		mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));

	}


	@Override
	public void onMyLocationClick(Location location) {
		if (addingStartMarker || addingDestinationMarker)
			addMarker(new LatLng(location.getLatitude(), location.getLongitude()));
	}


	@Override
	public void onMapClick(LatLng point) {
		if (addingStartMarker || addingDestinationMarker)
			addMarker(point);
		else
			alternateMenuVisibility();
	}


	private void addMarker(LatLng point) {
		// Add start marker
		if (addingStartMarker) {
			if (start != null)
				start.remove(); // Remove old one

			start = mMap.addMarker(new MarkerOptions()
					.position(point)
					.draggable(true)
					.title(getResources().getString(R.string.startTrip))
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

			addingStartMarker = false;
		}
		// Or add destination marker
		else {
			if (end != null)
				end.remove();   // Remove old one

			end = mMap.addMarker(new MarkerOptions()
					.position(point)
					.draggable(true)
					.title(getResources().getString(R.string.endTrip)));

			addingDestinationMarker = false;
		}
	}


	private void alternateMenuVisibility() {
		if (topBarAndSideBarVisible)
			hideTopBarAndSidebar();
		else
			showTopBarAndSidebar();
	}


	private void showTopBarAndSidebar() {
		if (!topBarAndSideBarVisible) {
			topBar.animate().translationYBy((float) topBar.getHeight());
			sideBar.animate().translationXBy((float) -sideBar.getWidth());
			mMap.setPadding(0, topBar.getHeight(), 0, 0);

			topBarAndSideBarVisible = true;
		}
	}


	private void hideTopBarAndSidebar() {
		if (topBarAndSideBarVisible) {
			topBar.animate().translationYBy((float) -topBar.getHeight());
			sideBar.animate().translationXBy((float) sideBar.getWidth());
			mMap.setPadding(0, 0, 0, 0);

			topBarAndSideBarVisible = false;
		}
	}


	public void onCloseClick(View view) {
		addingStartMarker = false;
		addingDestinationMarker = false;

		mMap.clear();
		start = null;
		end = null;

	}


	public void onAddStartLocationButtonClick(View view) {
		addingDestinationMarker = false;
		addingStartMarker = true;
	}


	public void onAddDestinationLocationButtonClick(View view) {
		addingDestinationMarker = true;
		addingStartMarker = false;
	}


	public void onGoClicked(View view) {
		if (start == null || end == null)
			Toast.makeText(getApplicationContext(), R.string.choseStartAndEnd, Toast.LENGTH_SHORT).show();
		else
			displayPath();
	}


	private void displayPath() {
		// Remove last path
		if (singlePath != null) {
			mMap.clear();
			start = mMap.addMarker(new MarkerOptions()
					.position(start.getPosition())
					.title(start.getTitle())
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

			end = mMap.addMarker(new MarkerOptions()
					.position(end.getPosition())
					.title(end.getTitle()));
		}

		// Multiple paths
		if (lineSpinner.getSelectedItemPosition() == 0) {
			multiplePaths = Path.shortestPaths(start.getPosition(), end.getPosition());
			if (multiplePaths.hasNext())
				singlePath = multiplePaths.next();
			else
				singlePath = null;
		}
		else {
			// Next best path
			if (checkingNextBestPath()) {
				if (multiplePaths.hasNext())
					singlePath = multiplePaths.next();
				else {
					lineSpinner.setSelection(0);
					singlePath = null;
				}
			}
			else {
				// Single path from spinner
				singlePath = Path.shortestPath(start.getPosition(), end.getPosition(), LineManager.getLine(lineSpinner.getSelectedItem().toString()));
				multiplePaths = null;
			}
		}

		if (singlePath != null) {
			displayRoute(singlePath.getLine());

			// Change the spinner to show the line's ID
			if (multiplePaths != null)
				lineSpinner.setSelection(lineIDs.indexOf(singlePath.getLine().lineID));

			// Add first stop
			String assetName;
			assetName = (singlePath.getFirstStop().isGo) ?
					"bus_stop_go.png" :
					"bus_stop_ret.png";

			mMap.addMarker(new MarkerOptions()
					.position(singlePath.getFirstStop().location)
					.title(getString(R.string.firstStop))
					.icon(BitmapDescriptorFactory.fromAsset(assetName)));

			// Add last stop
			assetName = (singlePath.getLastStop().isGo) ?
					"bus_stop_go.png" :
					"bus_stop_ret.png";

			mMap.addMarker(new MarkerOptions()
					.position(singlePath.getLastStop().location)
					.title(getString(R.string.lastStop))
					.icon(BitmapDescriptorFactory.fromAsset(assetName)));
		}
		else
			Toast.makeText(getApplicationContext(), R.string.noRouteAvaliable, Toast.LENGTH_SHORT).show();

	}


	private boolean checkingNextBestPath() {
		return  multiplePaths != null &&
				singlePath != null &&
				singlePath.getLine().lineID.equals(lineSpinner.getSelectedItem().toString()) &&
				singlePath.getStartLocation().equals(start.getPosition()) &&
				singlePath.getEndLocation().equals(end.getPosition());
	}

}
