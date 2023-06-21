package com.example.instrukcije;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap map;
	SupportMapFragment mapFragment;
	EditText inputField;
	ImageButton searchButton, saveButton;

	String location, hash;
	double lat, lng;

	FirebaseFirestore firestore;
	String userID;
	DocumentReference userDoc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		inputField = (EditText) findViewById(R.id.inputField);
		searchButton = (ImageButton) findViewById(R.id.searchLocationButton);
		saveButton = (ImageButton) findViewById(R.id.saveLocationButton);
		mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap);

		firestore = FirebaseFirestore.getInstance();
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		userDoc = firestore.collection("Users").document(userID);

		searchButton.setOnClickListener(view -> {
			location = inputField.getText().toString();
			List<Address> addresses;

			if (!location.isEmpty()) {
				Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

				try {
					addresses = geocoder.getFromLocationName(location, 1);
					if (addresses.size() > 0) {
						LatLng latLng = new LatLng(addresses.get(0).getLatitude(),
								addresses.get(0).getLongitude());
						lat = addresses.get(0).getLatitude();

						lng = addresses.get(0).getLongitude();
						hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));

						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.title(location);
						markerOptions.position(latLng);
						map.addMarker(markerOptions);
						CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
						map.animateCamera(cameraUpdate);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(MapsActivity.this, "Upišite ime grada", Toast.LENGTH_SHORT).show();
			}
		});

		saveButton.setOnClickListener(view -> {
			HashMap<String, Object> geo = new HashMap<>();

			String city = location.substring(0, location.indexOf(","));

			geo.put("geohash", hash);
			geo.put("lat",lat);
			geo.put("lng", lng);
			geo.put("location", location);
			geo.put("city", city);

			userDoc.update(geo);

			finish();
		});

		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
	}
}