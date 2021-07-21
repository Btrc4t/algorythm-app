package com.buttercat.algorythmhub.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.buttercat.algorythmhub.R;
import com.buttercat.algorythmhub.databinding.MainActivityBinding;
import com.buttercat.algorythmhub.viewmodel.MainActivityViewModel;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    /**
     * A map with entries having a resource ID from {@link R.menu#navigation} as a key and the
     * corresponding {@link Fragment}
     */
    private Map<Integer, Fragment> menuFragmentsMapping = new ArrayMap<>();
    /**
     * A key used to save the currently selected menu item from {@link R.menu#navigation} when
     * {@link #onSaveInstanceState(Bundle)} is called
     */
    private static final String NAVIGATION_MENU = "nav_id";
    /**
     * The last selected menu item from {@link R.menu#navigation}
     */
    private int lastSelectedItem = R.id.nav_nodes_list;
    //TODO save last used node as parcelable

    /**
     * {@link R.id#navigation} listener which changes the shown fragment based
     * on the selected item
     */
    private final NavigationBarView.OnItemSelectedListener mOnNavigationListener
            = item -> {
        lastSelectedItem = item.getItemId();
        FragmentTransaction menuChangeTransaction = getSupportFragmentManager().beginTransaction();
        for (Map.Entry<Integer, Fragment> entry : menuFragmentsMapping.entrySet()) {
            Integer resId = entry.getKey();
            Fragment fragment = entry.getValue();
            if (resId == item.getItemId()) {
                menuChangeTransaction.show(fragment);
            } else {
                menuChangeTransaction.hide(fragment);
            }
        }
        hideKeyboard();
        menuChangeTransaction.commit();
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityBinding mainActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.main_activity);
        menuFragmentsMapping.put(R.id.nav_nodes_list, NodeListFragment.newInstance());
        menuFragmentsMapping.put(R.id.nav_control_panel, ControlPanelFragment.newInstance());
        menuFragmentsMapping.put(R.id.nav_preferences, PreferencesFragment.newInstance());
        if (savedInstanceState != null) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
            getSupportFragmentManager().executePendingTransactions();
            lastSelectedItem = savedInstanceState.getInt(NAVIGATION_MENU);
        }
        initAllFragments(lastSelectedItem);
        MainActivityViewModel mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mViewModel.getSelectedNodeLiveData().observe(this, esp32Node -> {
            //TODO menuFragmentsMapping.get(R.id.nav_recipes_list).
            mainActivityBinding.navigation.setSelectedItemId(R.id.nav_control_panel);
        });
        mainActivityBinding.navigation.setOnItemSelectedListener(mOnNavigationListener);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(NAVIGATION_MENU, lastSelectedItem);
        //TODO save last used node as parcelable
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes all of the fragments shown in the application in background, keeping only one
     * visible
     *
     * @param idOfInitFragment Startup menu item from {@link R.menu#navigation}
     *                         for which the corresponding {@link Fragment} is kept visible
     */
    private void initAllFragments(int idOfInitFragment) {
        FragmentTransaction initFragmentTransaction = getSupportFragmentManager().beginTransaction();

        for (Map.Entry<Integer, Fragment> entry : menuFragmentsMapping.entrySet()) {
            Integer resId = entry.getKey();
            Fragment fragment = entry.getValue();
            initFragmentTransaction.add(R.id.container, fragment, String.valueOf(resId));
            if (resId != idOfInitFragment) {
                initFragmentTransaction.hide(fragment);
            }
        }
        initFragmentTransaction.commit();
    }
}
