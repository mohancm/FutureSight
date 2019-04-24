package com.bmsit.futuresight;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    Button buttonStart;
    Button buttonStop;
    EditText locationSearch;


    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = findViewById(R.id.buttonView);
        buttonStop = findViewById(R.id.buttonView);

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mSeekbar = rootRef.child("slider");
        final DatabaseReference mSearchBar = rootRef.child("search");
        //final String seekProgress = "seeking";

        //Check whether GPS tracking is enabled//

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

        //Check whether this app has access to the location permission//


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //startTrackerService();

            buttonStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTrackerService();

                    // Search location
                    locationSearch = findViewById(R.id.SearchBox);
                    String locationsData = locationSearch.getText().toString();
                    mSearchBar.child("locations").setValue(String.valueOf(locationsData));

                }
            });
        } else {

            //If the app doesn’t currently have access to the user’s location, then request access//

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

        //buttonStart.setOnClickListener();


        SeekBar seekBar = findViewById(R.id.seekBar2);
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Write code to perform some action when progress is changed.
                    int seekValue = seekBar.getProgress();
                    mSeekbar.child("value").setValue(String.valueOf(seekValue));
                    //rootRef.setValue(seekProgress);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Write code to perform some action when touch is started.
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Write code to perform some action when touch is stopped.
                    TextView startText = findViewById(R.id.textViewStart);
                    startText.setText("" + seekBar.getProgress());
                    Toast.makeText(MainActivity.this, "Current value is set to : " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();


                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        //If the permission has been granted...//

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //...then start the GPS tracking service//

            startTrackerService();
        } else {

            //If the user denies the permission request, then display a toast with some more information//

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    //Start the TrackerService//

    private void startTrackerService() {
        startService(new Intent(this, TrackingService.class));

        // Notify the user that tracking has been enabled//

        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

        finish();
    }

}
