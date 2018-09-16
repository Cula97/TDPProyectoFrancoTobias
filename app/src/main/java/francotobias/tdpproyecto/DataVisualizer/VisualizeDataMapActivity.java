package francotobias.tdpproyecto.DataVisualizer;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import francotobias.tdpproyecto.Bus;
import francotobias.tdpproyecto.BusManager;
import francotobias.tdpproyecto.Line;
import francotobias.tdpproyecto.LineManager;
import francotobias.tdpproyecto.MainActivity;
import francotobias.tdpproyecto.R;
import francotobias.tdpproyecto.Route;
import francotobias.tdpproyecto.Section;
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

		String lineID = getIntent().getStringExtra(MainActivity.DEBUG_LINE);
		line = LineManager.getLine(lineID);
		if (line == null) {
			Toast.makeText(getApplicationContext(), lineID + " no existe", Toast.LENGTH_SHORT).show();
			return;
		}

		goSectionIndex = 0;
		retSectionIndex = 0;
		goSectionAmount = line.getRoute().getGo().size();
		retSectionAmount = line.getRoute().getReturn().size();
		stopIndex = 0;

		if (line.getRoute().getStops() != null) {
			stopAmount = line.getRoute().getStops().size();
		}
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
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-38.7171, -62.2655), 14));

		String mode = getIntent().getStringExtra(MainActivity.DEBUG_MODE);
		displayModeInterface(mode);
	}


	private void displayModeInterface(String mode) {
		switch (mode) {
			case "Route":
				displayRouteInterface();
				break;
			case "Bus":
				displayBusInterface();
				break;
			case "Section":
				displaySectionInterface();
				break;
		}
	}

	private void displayRouteInterface() {
		findViewById(R.id.buttonSection).setVisibility(View.VISIBLE);
		findViewById(R.id.buttonStop).setVisibility(View.VISIBLE);
	}


	private void displayBusInterface() {
		displayRoutesWithBearings();
		displayBusesWithBearings();
	}

	private void displaySectionInterface() {
		findViewById(R.id.checkBoxSectionsGo).setVisibility(View.VISIBLE);
		findViewById(R.id.checkBoxSectionsReturn).setVisibility(View.VISIBLE);

		displayRouteButNowInAProperWay();

		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				mMap.clear();
				displayRouteButNowInAProperWay();
			}
		});

		// Change the Line Listener
		mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
			@Override
			public void onPolylineClick(Polyline polyline) {
				Section section = (Section) polyline.getTag();
				if (section.getStops() != null)
					mMap.clear();
				displayStopsWithDetails(section);
			}
		});
	}


	private void displayRoutesWithBearings() {
		mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
			@Override
			public void onPolylineClick(Polyline polyline) {
				Toast.makeText(getApplicationContext(), polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
			}
		});

		Iterator<LatLng> iteratorGo = line.getRoute().getGo().iterator();
		LatLng latLng1 = iteratorGo.next();
		LatLng latLng2;

		while (iteratorGo.hasNext()) {
			latLng2 = iteratorGo.next();
			displaySectionAndBearing(latLng1, latLng2, Color.BLUE);
			latLng1 = latLng2;
		}

		Iterator<LatLng> iteratorRet = line.getRoute().getReturn().iterator();
		latLng1 = iteratorRet.next();

		while (iteratorRet.hasNext()) {
			latLng2 = iteratorRet.next();
			displaySectionAndBearing(latLng1, latLng2, Color.RED);
			latLng1 = latLng2;
		}
	}


	private void displayBusesWithBearings() {
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				return false;
			}
		});

		Iterable<Bus> buses = line.updateBuses();

		AssetManager assetManager = getAssets();
		Bitmap icon = null;
		try {
			InputStream istream = assetManager.open("bus.png");
			icon = BitmapFactory.decodeStream(istream);
		} catch (IOException e) {
		}

		Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);

		for (Bus bus : buses) {
			LatLng position = new LatLng(bus.getLocation().getLatitude(), bus.getLocation().getLongitude());

			mMap.addMarker(new MarkerOptions()
					.position(position)
					.icon(BitmapDescriptorFactory.fromBitmap(scaledIcon))
					.title(Float.toString(bus.getLocation().getBearing())));
		}
	}


	private void displaySectionAndBearing(LatLng latLng1, LatLng latLng2, int color) {
		Location loc1 = new Location("");
		Location loc2 = new Location("");

		float bearing;

		loc1.setLatitude(latLng1.latitude);
		loc1.setLongitude(latLng1.longitude);

		loc2.setLatitude(latLng2.latitude);
		loc2.setLongitude(latLng2.longitude);

		bearing = loc1.bearingTo(loc2);

		mMap.addPolyline(new PolylineOptions()
				.add(latLng1, latLng2)
				.clickable(true)
				.color(color))
				.setTag(bearing);
	}


	public void displaySection(View view) {
		List<LatLng> routeGo = line.getRoute().getGo();
		List<LatLng> routeRet = line.getRoute().getReturn();

		if (goSectionIndex < goSectionAmount - 1)
			mMap.addPolyline(new PolylineOptions()
					.add(routeGo.get(goSectionIndex), routeGo.get(++goSectionIndex))
					.color(Color.BLUE));
		else if (retSectionIndex < retSectionAmount - 1)
			mMap.addPolyline(new PolylineOptions()
					.add(routeRet.get(retSectionIndex), routeRet.get(++retSectionIndex))
					.color(Color.RED));
		else
			Toast.makeText(getApplicationContext(), "Recorrido finalizado", Toast.LENGTH_SHORT).show();
	}


	// Muestra las paradas de a una usando los metodos viejos
	public void displayStop(View view) {
		if (stopIndex < stopAmount) {
			List<Stop> stops = line.getRoute().getStops();

			String type = "_ret";
			if (stops.get(stopIndex).isGo())
				type = "_go";

			AssetManager assetManager = getAssets();
			Bitmap icon = null;
			try {
				InputStream istream = assetManager.open("bus_stop" + type + ".png");
				icon = BitmapFactory.decodeStream(istream);
			} catch (IOException e) {
			}

			Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);

			mMap.addMarker(new MarkerOptions()
					.position(stops.get(stopIndex).getLocation())
					.icon(BitmapDescriptorFactory.fromBitmap(scaledIcon))
					.flat(true));

			stopIndex++;        // Demasiadas paradas en pantalla
		} else
			Toast.makeText(getApplicationContext(), "No hay más paradas", Toast.LENGTH_SHORT).show();
	}

	// Muestra las paradas asociadas a una seccion
	private void displayStopsWithDetails(Section section) {
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				return false;
			}
		});

		List<Stop> stops = section.getStops();

		int color = Color.RED;
		String type = "_ret";
		if (section.isGo) {
			type = "_go";
			color = Color.BLUE;
		}

		mMap.addPolyline(new PolylineOptions()
				.add(section.startPoint, section.endPoint)
				.color(color));

		AssetManager assetManager = getAssets();
		Bitmap icon = null;
		try {
			InputStream istream = assetManager.open("bus_stop" + type + ".png");
			icon = BitmapFactory.decodeStream(istream);
		} catch (IOException e) {
		}

		Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);

		Integer i = 0;
		Location newLoc, prevLoc = BusManager.latLngToLocation(section.startPoint, null);
		Float distance;

		if (stops == null) {
			Toast.makeText(getApplicationContext(), "Tramo sin paradas", Toast.LENGTH_SHORT).show();
			return;
		}

		for (Stop stop : stops) {
			newLoc = BusManager.latLngToLocation(stop.location, null);
			distance = prevLoc.distanceTo(newLoc);

			mMap.addMarker(new MarkerOptions()
					.position(stop.location)
					.icon(BitmapDescriptorFactory.fromBitmap(scaledIcon))
					.title((++i).toString())
					.snippet(distance.toString()));

			prevLoc = newLoc;
		}
	}


	// Usa las secciones para crear la ruta, cada tramo tiene una una polylinea con su seccion asociada
	private void displayRouteButNowInAProperWay() {
		List<Section> sectionsGo = line.getRoute().getSectionsGo();
		List<Section> sectionsRet = line.getRoute().getSectionsReturn();
		boolean showGo = ((CheckBox) findViewById(R.id.checkBoxSectionsGo)).isChecked();
		boolean showRet = ((CheckBox) findViewById(R.id.checkBoxSectionsReturn)).isChecked();

		if (showGo)
			for (Section section : sectionsGo) {
				mMap.addPolyline(new PolylineOptions()
						.add(section.startPoint, section.endPoint)
						.color(Color.BLUE)
						.clickable(true))
						.setTag(section);
			}

		if (showRet)
			for (Section section : sectionsRet) {
				mMap.addPolyline(new PolylineOptions()
						.add(section.startPoint, section.endPoint)
						.color(Color.RED)
						.clickable(true))
						.setTag(section);
			}

    }

}
