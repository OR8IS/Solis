package com.example.nijatmcmudow.solis;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeatherData nCurrentWeatherData;

    @InjectView(R.id.timeLabel) TextView nTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView nTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView nHumidityValue;
    @InjectView(R.id.precipValue) TextView nPrecipValue;
    @InjectView(R.id.summaryLabel) TextView nSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView nIconImageView;
    @InjectView(R.id.refreshImageView) ImageView nRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar nProgressBar;

    @Override
    // Creating and executing a background threat
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        nProgressBar.setVisibility(View.INVISIBLE);


        final double latitude = 55.6761;
        final double longitude = 12.5683;

        nRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude,longitude);
            }
        });

        getForecast(latitude,longitude);

        Log.d(TAG, "Main UI code is running");

    }

    private void getForecast(double latitude, double longitude ) {
        String apiKey = "8a1ba28e1e16aa17b906035aa96e5b32";
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        // When this if condition is checked, the isNetworkAvailable code will run.
        // And if it returns true, then our HTTP request will run too.
        if (isNetworkAvailable()) {
            toggleRefresh();

            // Client and Request build with URL.
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            // We create a call object and put the Request inside it.
    /*    Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                Log.v(TAG, response.body().string());
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);
        }
    */
            Call call = client.newCall(request);
            // Enqueue method executes the Call in the background by putting it in queue
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    // #handler# could have been used in stead of #runOnUiThread. Especially useful if you planning to run
                    // an background activity later on.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            nCurrentWeatherData = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (nProgressBar.getVisibility() == View.INVISIBLE) {
            nProgressBar.setVisibility(View.VISIBLE);
            nRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
            nProgressBar.setVisibility(View.INVISIBLE);
            nRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        nTemperatureLabel.setText(nCurrentWeatherData.getnTemperature() + "");
        nTimeLabel.setText("At " + nCurrentWeatherData.getfTime() + " it well be");
        nHumidityValue.setText(nCurrentWeatherData.getnHumidity() + "");
        nPrecipValue.setText(nCurrentWeatherData.getnPrecipChance() + "%");
        nSummaryLabel.setText(nCurrentWeatherData.getnSummary());

        Drawable drawable = getResources().getDrawable(nCurrentWeatherData.getIconId());
        nIconImageView.setImageDrawable(drawable);
    }

    private CurrentWeatherData getCurrentDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeatherData currentWeatherData = new CurrentWeatherData();
        currentWeatherData.setnHumidity(currently.getDouble("humidity"));
        currentWeatherData.setnTime(currently.getLong("time"));
        currentWeatherData.setnIcon(currently.getString("icon"));
        currentWeatherData.setnPrecipChance(currently.getDouble("precipProbability"));
        currentWeatherData.setnSummary(currently.getString("summary"));
        currentWeatherData.setnTemperature(currently.getDouble("temperature"));
        currentWeatherData.setnTimeZone(timezone);

        Log.d(TAG, currentWeatherData.getfTime());

        return currentWeatherData;
    }

    // Error handling when network isn't available
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    // Error handling when something goes wrong with network request
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    //LOCATION MANAGER IN PROGRESS...
/*  LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    double longitude = location.getLongitude();
    double latitude = location.getLatitude();

    private final LocationListener locationListener = new LocationListener() {
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }
}
    // permissions
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    // method
    getBestProvider() method.
*/

}
