package prestozen.presto.watch.prestozen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mapzen.pelias.Pelias;
import com.mapzen.pelias.gson.Feature;
import com.mapzen.pelias.gson.Result;
import com.mapzen.pelias.widget.PeliasSearchView;
import com.mapzen.valhalla.Instruction;
import com.mapzen.valhalla.Route;
import com.mapzen.valhalla.RouteCallback;
import com.mapzen.valhalla.ValhallaRouter;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainPhoneActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainPhoneActivity";
    private PeliasSearchView searchView;
    private GoogleApiClient googleApiClient;

    private Location myLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_phone);
        ViewGroup layout = (ViewGroup) findViewById(R.id.main_relative_layout);
        Pelias pelias = Pelias.getPelias();
        pelias.setApiKey("search-CO7N6XU");
        searchView = new PeliasSearchView(this);
        searchView.setPelias(pelias);
        //TODO: Set API key for Pelias
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layout.addView(searchView, params);
        searchView.setCallback(new Callback<Result>() {
            @Override
            public void success(Result result, Response response) {
                List<Feature> features = result.getFeatures();
                if (features == null || features.size() < 1) {
                    Toast.makeText(MainPhoneActivity.this,
                            "No lat, lng found for address", Toast.LENGTH_LONG).show();
                    //TODO: reset searchView
                } else {
                    Feature f = features.get(0);
                    double lat = f.geometry.coordinates.get(0);
                    double lng = f.geometry.coordinates.get(1);

                    double[] start = {myLoc.getLatitude(), myLoc.getLongitude()};
                    double[] dest = {lat, lng};

                    ValhallaRouter router = new ValhallaRouter();
                    router.setDriving().
                            setLocation(start).
                            setLocation(dest).
                            setApiKey("valhalla-MQoLbSQ").setCallback(new RouteCallback() {
                        @Override
                        public void success(Route route) {
                            List<Instruction> instructions = route.getRouteInstructions();
                            ArrayList<String> dirs = new ArrayList<String>(instructions.size());
                            for (Instruction instruction : instructions) {
                                dirs.add(instruction.getVerbalPreTransitionInstruction());
                            }
                            //TODO: send this to Gesture Service using a local broadcast
                        }

                        @Override
                        public void failure(int i) {
                            Toast.makeText(MainPhoneActivity.this,
                                    "Call to router failed", Toast.LENGTH_LONG).show();

                        }
                    }).fetch();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //TODO: Do we need to set visiblilty of progress bar to gone?
                Toast.makeText(MainPhoneActivity.this, "Call to Pelias failed", Toast.LENGTH_LONG).show();
            }
        });
        searchView.setEnabled(false);//Don't enable search view util we have location
        //get the current location
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
        Intent intent = new Intent(this, GestureCallbackService.class);
        startService(intent);
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        Intent intent = new Intent(this, GestureCallbackService.class);
        stopService(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        myLoc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(myLoc != null)
            searchView.setEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "Connection suspended GPS:LocationServices");
        Toast.makeText(MainPhoneActivity.this,
                "Unable to get current location, connection suspended", Toast.LENGTH_LONG).show();
        searchView.setEnabled(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "Failed to connect with GPS:LocationServices");
        Toast.makeText(MainPhoneActivity.this,
                "Unable to get current location, connection failed", Toast.LENGTH_LONG).show();
        searchView.setEnabled(false);
    }
}
