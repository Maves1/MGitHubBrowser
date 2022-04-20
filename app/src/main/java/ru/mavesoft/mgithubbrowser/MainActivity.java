package ru.mavesoft.mgithubbrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mavesoft.mgithubbrowser.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    Map<Integer, Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragments = createFragments();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(fragments.get(R.id.menu_home));

        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            try {
                replaceFragment(fragments.get(item.getItemId()));
            } catch (Exception ex) {
                Log.e("BottomBarError", "Cannot find requested fragment");

                return false;
            }

            return true;
        });
    }

    private Map<Integer, Fragment> createFragments() {
        Map<Integer, Fragment> fragments = new HashMap<>();

        fragments.put(R.id.menu_home, new HomeFragment());
        fragments.put(R.id.menu_search, new SearchFragment());

        return fragments;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.currentLayout, fragment);
        fragmentTransaction.commit();
    }
}