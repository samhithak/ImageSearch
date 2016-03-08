package samhithak.com.imagegallery;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GalleryFragment.GalleryFragmentListener {
    public static final String IMAGE = "Image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new GalleryFragment();
        ft.add(R.id.fragment_container, fragment);
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_gallery) {
            fragment = new GalleryFragment();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onImageClicked(Image image) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IMAGE, image);
        fragment.setArguments(bundle);
        ft.addToBackStack(null);
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();

    }
}
