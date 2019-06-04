package de.backesdill.backesapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.backesdill.backesapp.Fragments.FControl;
import de.backesdill.backesapp.Fragments.FControlBackes;
import de.backesdill.backesapp.Fragments.FDebug;
import de.backesdill.backesapp.Fragments.FDisplay;
import de.backesdill.backesapp.Fragments.FPfingste;
import de.backesdill.backesapp.Fragments.FStartScreen;
import de.backesdill.helper.ListStorage;
import de.backesdill.helper.NetDB;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListStorage mConsoleOutput;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mConsoleOutput = new ListStorage();
        mConsoleOutput.add(false,"FPfingste on ActivityCreated");

        // create the netDB instance so the network module ist up and running
        try {
            NetDB netDB = NetDB.getNetDB();
        } catch (Exception e){
            mConsoleOutput.add(true, "MainActivity getNetDB() exception " + e);
        }

    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        Fragment fragment = null;
        Class fragmentClass;
        switch(item.getItemId()) {
            case R.id.nav_home:
                fragmentClass = FStartScreen.class;
                //sScreenTV.setText("Startseite");
                break;
            case R.id.nav_bf_display:
                fragmentClass = FPfingste.class;
                break;
            case R.id.nav_ff_display:
                fragmentClass = FDisplay.class;
                break;
            case R.id.nav_control_feuerwehrfest:
                fragmentClass = FControl.class;
                break;
            case R.id.nav_control:
                fragmentClass = FControlBackes.class;
                break;
            case R.id.nav_debug:
                fragmentClass = FDebug.class;
                break;
            default:
                fragmentClass = FStartScreen.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
