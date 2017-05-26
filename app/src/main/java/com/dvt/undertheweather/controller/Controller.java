package com.dvt.undertheweather.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dvt.undertheweather.R;
import com.dvt.undertheweather.model.SpinnerNavItem;
import com.dvt.undertheweather.model.Weather;
import com.dvt.undertheweather.utilis.ConnectionDetector;
import com.dvt.undertheweather.model.Category;
import com.dvt.undertheweather.utilis.JSONWeatherParser;
import com.dvt.undertheweather.utilis.WeatherHttpClient;
import com.dvt.undertheweather.view.HomeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin on 5/25/2017.
 */

public class Controller extends AppCompatActivity  {
    private Context context;
    private AppCompatActivity activity;
    ConnectionDetector condetect;
    ActionBar actionBar;
    private Spinner spinnertoolbar;
    private int titlePosition;
    View rootView;
    private int titleID = 1;
    private ArrayList<Category> menuList;
    private ArrayList<SpinnerNavItem> navSpinner;
    private ArrayAdapter adapter;
    String city = "";
    String country = "";
    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView dateTime;

    private TextView maxText;
    private TextView minText;
    private TextView hum;
    private ImageView imgView;

    Boolean dataSet = false;

    public void getFragment(final Context context, int position) {
        this.context = context;

        menuList = new ArrayList<Category>();
        //check for internet connection
        condetect = new ConnectionDetector(context);
        if(!condetect.isConnectingToInternet()) {
            // Error in connection
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Internet Connection Error!");
                    alertDialog.setMessage("Please Connect to the Internet and retry or press cancel to try again later.")
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Controller routes = new Controller();
                                    routes.goToHomePage(context);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    // Create the AlertDialog object and return it
                    alertDialog.create();
                    alertDialog.show();
                }
            });
        } else {
            actionBar =((AppCompatActivity) context).getSupportActionBar();
            spinnertoolbar = (Spinner) ((AppCompatActivity) context).findViewById(R.id.spinner_toolbar);
            titlePosition = position;
            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

            Location location = null;


            try {
                location = lm.getLastKnownLocation(lm.NETWORK_PROVIDER);
                if (location == null){
                    location = lm.getLastKnownLocation(lm.GPS_PROVIDER);
                }

            } catch (SecurityException e) {
            }
            if (location != null){
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0)
                {
                    city = addresses.get(0).getLocality();
                    country = addresses.get(0).getCountryName();
                }
                else
                {
                }
            }


            populateHome();
        }
    }


    private void populateHome() {
        rootView = null;
        setTitle(titleID);
        if (titlePosition == 1){
            rootView = ((AppCompatActivity) context).findViewById(R.id.container_home);
            JSONWeatherTask task = new JSONWeatherTask();
            task.execute(new String[]{city});
        } else if (titlePosition == 2){
        } else if (titlePosition == 3){
        }
    }
    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cityText = (TextView) rootView.findViewById(R.id.cityText);
            dateTime = (TextView) rootView.findViewById(R.id.dateTime);
            condDescr = (TextView) rootView.findViewById(R.id.condDescr);
            temp = (TextView) rootView.findViewById(R.id.temp);
            maxText = (TextView) rootView.findViewById(R.id.maxText);
            minText = (TextView) rootView.findViewById(R.id.minText);
            imgView = (ImageView) rootView.findViewById(R.id.condIcon);
        }
        //        @OverrideWeatherHttpClient.java
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                if (data != null){
                    weather = JSONWeatherParser.getWeather(data);
                    dataSet = true;
                    // Let's retrieve the icon
                    weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }

            if (dataSet){
                cityText.setText(city + "," + country);
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("temp " + Math.round((weather.temperature.getTemp() - 273.15)) + (char) 0x00B0+"C");
                maxText.setText("max " + Math.round((weather.temperature.getMaxTemp() - 273.15)) + (char) 0x00B0+"C");
                minText.setText("min " + Math.round((weather.temperature.getMinTemp() - 273.15)) + (char) 0x00B0+"C");
            }
            SimpleDateFormat sdfsql = new SimpleDateFormat("dd MMMM yyyy");
            String currentDateandTime = sdfsql.format(new Date());
            dateTime.setText("Today, "+currentDateandTime);
        }
    }

    public void goToHomePage(Context context){
        Intent i = new Intent(context, HomeView.class);
        context.startActivity(i);
    }

    @SuppressWarnings("deprecation")
    public void setTitle(int title) {
        new addMenuList().execute();
    }


    private class addMenuList extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0){
            //reset the menu list
            menuList.removeAll(menuList);
            String [] arrayMenuList = context.getResources().getStringArray(R.array.array_menu_list);

            if (arrayMenuList != null) {
                for (int i = 0; i < arrayMenuList.length; i++) {
                    Category cat = new Category(arrayMenuList[i]);
                    menuList.add(cat);
                }
//                    pDialog.dismiss();

            } else {
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            // dismiss the dialog once done
            super.onPostExecute(result);
//            if (pDialog.isShowing())
//                pDialog.dismiss();
            if (!menuList.isEmpty()) {
                addItemsOnMenu();
            }
        }
    }
    @SuppressWarnings("deprecation")
    public void addItemsOnMenu() {
        navSpinner = new ArrayList<SpinnerNavItem>();
        actionBar = ((AppCompatActivity) context).getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
        ArrayList<String> nav_bar = new ArrayList<String>();
        nav_bar.add(0, "- Select -");
        for (int i = 0; i < menuList.size(); i++) {
            nav_bar.add(menuList.get(i).getName());
        }
        if (menuList.size() > 0) {
            // title drop down adapter
            adapter = new ArrayAdapter(context, R.layout.layout_drop_list, nav_bar);
            adapter.setDropDownViewResource(R.layout.layout_drop_list);
            spinnertoolbar.setAdapter(adapter);
            spinnertoolbar.setSelection(titleID);
            spinnertoolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (titleID != position) {
                        titleID = position;
                        getFragment(context, titleID);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}

