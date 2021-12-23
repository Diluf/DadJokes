package com.debugsire.dadjokes;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.debugsire.dadjokes.ui.Favorites.FavoritesFragment;
import com.debugsire.dadjokes.ui.Home.HomeFragment;
import com.debugsire.dadjokes.ui.Downloads.DownloadsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity -- ";

    HomeFragment homeFragment = new HomeFragment();
    FavoritesFragment favoritesFragment = new FavoritesFragment();
    DownloadsFragment downloadsFragment = new DownloadsFragment();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        BottomNavigationView navView = findViewById(R.id.nav_view);



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

        getSupportActionBar().hide();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, downloadsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, favoritesFragment).commit();
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment,homeFragment).commit();
        //
        fragmentManager.beginTransaction().hide(favoritesFragment).commit();
        fragmentManager.beginTransaction().hide(downloadsFragment).commit();



        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected: " + item.getItemId());
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
                        active = homeFragment;
                        break;
                    case R.id.navigation_fav:
                        favoritesFragment.loadRv();
                        fragmentManager.beginTransaction().hide(active).show(favoritesFragment).commit();
                        active = favoritesFragment;
                        break;
                    case R.id.navigation_down:
                        downloadsFragment.loadRv();
                        fragmentManager.beginTransaction().hide(active).show(downloadsFragment).commit();
                        active = downloadsFragment;
                        break;
                }

                return true;
            }
        });

    }

}