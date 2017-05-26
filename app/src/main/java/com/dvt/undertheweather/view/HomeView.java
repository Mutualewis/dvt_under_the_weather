package com.dvt.undertheweather.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dvt.undertheweather.R;
import com.dvt.undertheweather.controller.Controller;

public class HomeView extends AppCompatActivity {
    private Context context;
    private int titlePosition = 1;
    Controller controller = new Controller();
    private static ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(HomeView.this);
        setPDialog(pDialog);
        setContentView(R.layout.activity_home_view);

        //get the selected fragment
        context = HomeView.this;
        controller.getFragment(context, titlePosition);

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.logo_dvt);
    }

    public void setPDialog(ProgressDialog pDialog) {
        HomeView.pDialog = pDialog;
    }

    public ProgressDialog getPDialog() {
        return pDialog;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_menuRefresh:
//                controller.getFragment(context, titlePosition);
                Intent intent = new Intent(HomeView.this, HomeView.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
