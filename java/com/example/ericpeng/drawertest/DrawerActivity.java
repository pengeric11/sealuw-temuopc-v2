package com.example.ericpeng.drawertest;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener,
        HistoryFragment.OnFragmentInteractionListener, RecordFragment.OnFragmentInteractionListener,
        ConnectedFragment.OnFragmentInteractionListener{

    public static int REQUEST_BLUETOOTH = 1;
    public BluetoothAdapter bTAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        HomeFragment main_frag = new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, main_frag).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectDrawerItem(item);
        } else if (id == R.id.nav_bluetooth) {
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));

        } else if (id == R.id.nav_dropbox) {
            Uri uri = Uri.parse("http://dropbox.com/");

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        } else if (id == R.id.nav_history) {
           selectDrawerItem(item);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

   public void selectDrawerItem(MenuItem menuItem){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                HomeFragment frag1 = new HomeFragment();
                transaction.replace(R.id.flContent, frag1);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.nav_history:
                HistoryFragment frag2 = new HistoryFragment();
                transaction.replace(R.id.flContent, frag2);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            default:
                break;
        }

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());


    }

    public void record(View view){
        RecordFragment record = new RecordFragment();
        //getSupportFragmentManager().beginTransaction().replace(R.id.flContent, record).addToBackStack(null).commit();
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);

    }

    public void btSettings(View view){
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    public void addDevice(View view){
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }




}
