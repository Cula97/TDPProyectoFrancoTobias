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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import francotobias.tdpproyecto.Bus;
import francotobias.tdpproyecto.BusManager;
import francotobias.tdpproyecto.Line;
import francotobias.tdpproyecto.LineManager;
import francotobias.tdpproyecto.MainActivity;
import francotobias.tdpproyecto.Path;
import francotobias.tdpproyecto.R;
import francotobias.tdpproyecto.Section;
import francotobias.tdpproyecto.Stop;

public class VisualizeDataMapActivity extends FragmentActivity implements OnMapReadyCallback {
	private GoogleMap mMap;
	private Line line;

	private Set<Marker> displayedGoMarkers;
	private Set<Marker> displayedRetMarkers;
	private ArrayList<Stop> stopsGo;
	private ArrayList<Stop> stopsRet;

	private int goSectionIndex;
	private int goSectionAmount;
	private int retSectionIndex;
	private int retSectionAmount;
	private int stopGoIndex;
	private int stopRetIndex;
	private int stopAmount;

	private int stopCounter;
	Marker stopGoAux1, stopGoAux2;
	Marker stopRetAux1, stopRetAux2;
	private boolean sectionsGoFinished = false;
	private boolean sectionsRetFinished = false;

	private int pathTestNumber;

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

		((TextView) findViewById(R.id.textViewLine)).setText(lineID);

		goSectionIndex = 0;
		retSectionIndex = 0;
		goSectionAmount = line.getRoute().getGo().size();
		retSectionAmount = line.getRoute().getReturn().size();
		stopGoIndex = 0;
		stopRetIndex = 0;
		List<Stop> stops = line.getRoute().getStops();

		if (stops != null) {
			stopAmount = line.getRoute().getStops().size();
			displayedGoMarkers = new HashSet<>();
			displayedRetMarkers = new HashSet<>();
			stopsGo = new ArrayList<>();
			stopsRet = new ArrayList<>();

			for (Stop stop : stops)
				if (stop.isGo)
					stopsGo.add(stop);
				else
					stopsRet.add(stop);
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
			case "Path":
				displayPathInterface();
				break;
		}
	}

	private void displayRouteInterface() {
		findViewById(R.id.buttonSection).setVisibility(View.VISIBLE);
		findViewById(R.id.buttonStop).setVisibility(View.VISIBLE);
		findViewById(R.id.layoutOptions).setVisibility(View.VISIBLE);
		findViewById(R.id.checkBoxKeepStops).setVisibility(View.VISIBLE);
	}


	private void displayBusInterface() {
		displayRoutesWithBearings();
		displayBusesWithBearings();
	}

	private void displaySectionInterface() {
		findViewById(R.id.layoutOptions).setVisibility(View.VISIBLE);

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


	private void displayPathInterface() {
		findViewById(R.id.buttonPathTestNext).setVisibility(View.VISIBLE);
		findViewById(R.id.buttonPathTestPrev).setVisibility(View.VISIBLE);
		findViewById(R.id.textViewPathTest).setVisibility(View.VISIBLE);
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

		boolean showGo = ((CheckBox) findViewById(R.id.checkBoxSectionsGo)).isChecked();
		boolean showRet = ((CheckBox) findViewById(R.id.checkBoxSectionsReturn)).isChecked();

		if (showGo) {
			if (goSectionIndex < goSectionAmount - 1)
				mMap.addPolyline(new PolylineOptions()
						.add(routeGo.get(goSectionIndex), routeGo.get(++goSectionIndex))
						.color(Color.BLUE));
			else if (!sectionsGoFinished) {
				Toast.makeText(getApplicationContext(), "Recorrido de ida finalizado", Toast.LENGTH_SHORT).show();
				sectionsGoFinished = true;
			}

		}

		if (showRet) {
			if (retSectionIndex < retSectionAmount - 1)
				mMap.addPolyline(new PolylineOptions()
						.add(routeRet.get(retSectionIndex), routeRet.get(++retSectionIndex))
						.color(Color.RED));
			else if (!sectionsRetFinished) {
				Toast.makeText(getApplicationContext(), "Recorrido de vuelta finalizado", Toast.LENGTH_SHORT).show();
				sectionsRetFinished = true;
			}
		}
	}


	// Muestra las paradas de a una usando los metodos viejos
	public void displayStop(View view) {
		if (stopGoIndex + stopRetIndex == stopAmount -2 || stopAmount == 0) {
			Toast.makeText(getApplicationContext(), "No hay más paradas", Toast.LENGTH_SHORT).show();
			return;
		}

		boolean showGo = ((CheckBox) findViewById(R.id.checkBoxSectionsGo)).isChecked();
		boolean showRet = ((CheckBox) findViewById(R.id.checkBoxSectionsReturn)).isChecked();
		boolean keepStops = ((CheckBox) findViewById(R.id.checkBoxKeepStops)).isChecked();

		if (showGo)
			if (stopGoIndex < stopsGo.size()) {
				AssetManager assetManager = getAssets();
				Bitmap icon = null;
				try {
					InputStream istream = assetManager.open("bus_stop_go.png");
					icon = BitmapFactory.decodeStream(istream);
				} catch (IOException e) {}

				Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);

				Marker marker = mMap.addMarker(new MarkerOptions()
						.position(stopsGo.get(stopGoIndex++).location)
						.icon(BitmapDescriptorFactory.fromBitmap(scaledIcon))
						.title(stopsGo.get(stopGoIndex-1).location.toString()));

				if (stopCounter++ % 10 == 0 && !showRet)
					mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

				if (stopGoAux1 != null)
					stopGoAux1.setVisible(keepStops);

				if (stopGoAux2 != null && stopGoAux1.isVisible() != stopGoAux2.isVisible())
					for (Marker m : displayedGoMarkers)
						m.setVisible(keepStops);

				stopGoAux2 = stopGoAux1;
				stopGoAux1 = marker;
				displayedGoMarkers.add(marker);
			} else {
				Toast.makeText(getApplicationContext(), "No hay más paradas de ida", Toast.LENGTH_SHORT).show();
				((CheckBox) findViewById(R.id.checkBoxSectionsGo)).setChecked(false);
				if (keepStops)
					for (Marker m : displayedGoMarkers)
						m.setVisible(true);
			}
		else
			if (!keepStops && stopGoAux1 != null && stopGoAux1.isVisible())
				for (Marker m : displayedGoMarkers)
					m.setVisible(false);


		if (showRet)
			if (stopRetIndex < stopsRet.size()) {
				AssetManager assetManager = getAssets();
				Bitmap icon = null;
				try {
					InputStream istream = assetManager.open("bus_stop_ret.png");
					icon = BitmapFactory.decodeStream(istream);
				} catch (IOException e) {}

				Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);

				Marker marker = mMap.addMarker(new MarkerOptions()
						.position(stopsRet.get(stopRetIndex++).location)
						.icon(BitmapDescriptorFactory.fromBitmap(scaledIcon))
						.title(stopsRet.get(stopRetIndex-1).location.toString()));

				if (stopCounter++ % 10 == 0 && !showGo)
					mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

				if (stopRetAux1 != null)
					stopRetAux1.setVisible(keepStops);

				if (stopRetAux2 != null && stopRetAux1.isVisible() != stopRetAux2.isVisible())
					for (Marker m : displayedRetMarkers)
						m.setVisible(keepStops);

				stopRetAux2 = stopRetAux1;
				stopRetAux1 = marker;
				displayedRetMarkers.add(marker);
			} else {
				Toast.makeText(getApplicationContext(), "No hay más paradas de vuelta", Toast.LENGTH_SHORT).show();
				((CheckBox) findViewById(R.id.checkBoxSectionsReturn)).setChecked(false);
				if (keepStops)
					for (Marker m : displayedRetMarkers)
						m.setVisible(true);
			}
		else
			if (!keepStops && stopRetAux1 != null && stopRetAux1.isVisible())
				for (Marker m : displayedRetMarkers)
					m.setVisible(false);
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


    public void pathTestPrev(View view) {
		pathTestNumber -= 2;
		if (pathTestNumber == -2) pathTestNumber = 8;
		if (pathTestNumber == -1) pathTestNumber = 9;
		pathTestNext(view);
    }

    public void pathTestNext(View view) {
		LatLng start = null, end = null;
		String lineID = null, testString = null;
	    Path path;

	    mMap.clear();

	    switch (pathTestNumber++) {
		    // 500
	    	case 0:
				start = new LatLng(-38.752981, -62.266180);
				end = new LatLng(-38.701966, -62.270744);
				lineID = "500";
				testString = "Ida desde paradas";
		    	break;
		    case 1:
			    start = new LatLng(-38.749684, -62.262060);
			    end = new LatLng(-38.703716, -62.273561);
			    lineID = "500";
			    testString = "Ida a unas cuadras";
		    	break;
		    case 2:
			    start = new LatLng(-38.710168, -62.271081);
			    end = new LatLng(-38.739860, -62.275757);
			    lineID = "500";
			    testString = "Vuelta desde paradas";
			    break;
		    case 3:
			    start = new LatLng(-38.708335, -62.273409);
			    end = new LatLng(-38.742093, -62.278658);
			    lineID = "500";
			    testString = "Vuelta a unas cuadras";
			    break;
		    case 4:
			    start = new LatLng(-38.749684, -62.262060);
			    end = new LatLng(-38.703716, -62.273561);
			    lineID = "ANY";
			    testString = "Ida a unas cuadras 500";
		    	break;

			// 504
		    case 5:
			    start = new LatLng(-38.732851, -62.251388);
			    end = new LatLng(-38.749118, -62.282182);
			    lineID = "504";
			    testString = "Ida desde paradas";
			    break;
		    case 6:
			    start = new LatLng(-38.734630, -62.253609);
			    end = new LatLng(-38.746304, -62.278730);
			    lineID = "504";
			    testString = "Ida a unas cuadras";
			    break;
		    case 7:
			    start = new LatLng(-38.745866, -62.286506);
			    end = new LatLng(-38.727402, -62.254607);
			    lineID = "504";
			    testString = "Vuelta desde paradas";
			    break;
		    case 8:
			    start = new LatLng(-38.744988, -62.283349);
			    end = new LatLng(-38.732018, -62.257051);
			    lineID = "504";
			    testString = "Vuelta a unas cuadras";
			    break;
		    case 9:
			    start = new LatLng(-38.744988, -62.283349);
			    end = new LatLng(-38.732018, -62.257051);
			    lineID = "ANY";
			    testString = "Ida a unas cuadras 504";
			    pathTestNumber = 0;
			    break;
	    }

	    if (!lineID.equals("ANY")) {
	    	Line line = LineManager.getLine(lineID);
	    	path = Path.shortestPath(start, end, line);
	    }
	    else
	    	path = Path.shortestPaths(start, end).iterator().next();

	    line = path.getLine();

	    List<LatLng> routeGo = line.getRoute().getGo();
	    List<LatLng> routeRet = line.getRoute().getReturn();
	    mMap.addPolyline(new PolylineOptions()
			    .addAll(routeGo)
			    .color(Color.BLUE));
	    mMap.addPolyline(new PolylineOptions()
			    .addAll(routeRet)
			    .color(Color.RED));

	    ((TextView) findViewById(R.id.textViewLine)).setText(lineID);
	    ((TextView) findViewById(R.id.textViewPathTest)).setText(testString +"  "+ path.distance +"m");

	    mMap.addMarker(new MarkerOptions()
			    .position(start)
			    .title("Inicio"));


	    AssetManager assetManager = getAssets();
	    Bitmap icon = null;
	    try {
		    InputStream istream = assetManager.open("bus_stop.png");
		    icon = BitmapFactory.decodeStream(istream);
	    } catch (IOException e) {}

	    Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);


	    mMap.addMarker(new MarkerOptions()
			    .position(path.firstStop().location)
			    .title("Primer parada")
	            .icon(BitmapDescriptorFactory.fromBitmap(scaledIcon)));

	    mMap.addMarker(new MarkerOptions()
			    .position(path.lastStops().location)
			    .title("Ultima parada")
	            .icon(BitmapDescriptorFactory.fromAsset("bus_stop.png")));

	    mMap.addMarker(new MarkerOptions()
			    .position(end)
			    .title("Fin"));
    }

}
