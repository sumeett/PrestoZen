package prestozen.presto.watch.prestozen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mapzen.pelias.gson.Feature;
import com.mapzen.pelias.gson.Result;
import com.mapzen.pelias.widget.PeliasSearchView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainPhoneActivity extends AppCompatActivity {

    private PeliasSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_phone);
        ViewGroup layout = (ViewGroup) findViewById(R.id.main_relative_layout);
        searchView = new PeliasSearchView(this);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layout.addView(searchView, params);
        searchView.setCallback(new Callback<Result>() {
            @Override
            public void success(Result result, Response response) {
                List<Feature> features = result.getFeatures();
                result.getType()
                if(features == null && features.size() < 1) {
                    Toast.makeText(MainPhoneActivity.this,
                            "No lat, lng found for address", Toast.LENGTH_LONG).show();
                    //TODO: reset searchView
                } else {
                    Feature f = features.get(0);
                    double lat = f.geometry.coordinates.get(0);
                    double lng = f.geometry.coordinates.get(1);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                //TODO: Do we need to set visiblilty of progress bar to gone?
                Toast.makeText(MainPhoneActivity.this, "Call to Pelias failed", Toast.LENGTH_LONG).show();
            }
        });
        searchView.setEnabled(false);//Don't enable search view util we have location
    }
}
