package com.buttercat.algorythmhub.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.buttercat.algorythmhub.R;
import com.buttercat.algorythmhub.databinding.PreferencesFragmentBinding;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Prefs;
import com.buttercat.algorythmhub.viewmodel.PreferencesViewModel;
import timber.log.Timber;

/**
 * A custom {@link Fragment} which shows the
 * {@link com.buttercat.algorythmhub.view.utils.NodeListViewAdapter}
 */
public class PreferencesFragment extends Fragment {
    /**
     * The databinding class which takes care of inflating the fragment
     */
    private PreferencesFragmentBinding mBinding;
    private PreferencesViewModel mViewModel;
    private Prefs mPreferences;
    private static final int AMP_THRESH_MAX_VALUE_MAX = 4096;
    private static final int AMP_THRESH_MIN_VALUE_MAX = AMP_THRESH_MAX_VALUE_MAX-1;
    private static final int FREQ_MAX = 22050;
    private static final int COLOR_INTENSITY_MAX = 100;


    /**
     * Method returning a new instance of this class
     *
     * @return a new {@link PreferencesFragment} instance
     */
    /*package*/ static PreferencesFragment newInstance() {
        return new PreferencesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = PreferencesFragmentBinding
                .inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragment();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateData();
        }
    }

    private void initFragment() {
        Timber.d("onViewCreated: PreferencesFragment ");
        mViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);
        mBinding.setLifecycleOwner(this);
        mBinding.setModel(mViewModel);
        mPreferences = mViewModel.getPrefs();
        mBinding.setPrefs(mPreferences);
    }

    private void updateData() {
        ESP32Node selectedNode = mViewModel.getSelectedNode();
        if (selectedNode == null) {
            return;
        }
        if (mViewModel.checkConnection(selectedNode)) {
            if (selectedNode.getPrefs() == null) mViewModel.updatePreferences(mPreferences);
            mBinding.headingTitle.setText(selectedNode.getRoom());
            mBinding.editAmpMin.setText(String.valueOf(selectedNode.getPrefs().getAmpMin()));
            mBinding.editAmpMax.setText(String.valueOf(selectedNode.getPrefs().getAmpMax()));
            mBinding.editFreqBStart.setText(String.valueOf(selectedNode.getPrefs().getFreqBlueStart()));
            mBinding.editFreqBEnd.setText(String.valueOf(selectedNode.getPrefs().getFreqBlueEnd()));
            mBinding.editFreqGEnd.setText(String.valueOf(selectedNode.getPrefs().getFreqGreenEnd()));
            mBinding.editFreqREnd.setText(String.valueOf(selectedNode.getPrefs().getFreqRedEnd()));
            mBinding.editHoldModeInt.setText(String.valueOf(selectedNode.getPrefs().getAudioHoldIntensity()));
        } else {
            mBinding.headingTitle.setText(R.string.no_node_selected);
        }
    }
}
