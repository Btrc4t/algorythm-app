package com.buttercat.algorythmhub.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.buttercat.algorythmhub.R;
import com.buttercat.algorythmhub.databinding.PreferencesFragmentBinding;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Prefs;
import com.buttercat.algorythmhub.viewmodel.PreferencesViewModel;

/**
 * A custom {@link Fragment} which shows the
 * {@link com.buttercat.algorythmhub.view.utils.NodeListViewAdapter}
 */
public class PreferencesFragment extends Fragment {
    private static final String TAG = PreferencesFragment.class.getSimpleName();
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
        Log.d("TAG", "onViewCreated: PreferencesFragment ");
        mViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);
        mBinding.setLifecycleOwner(this);
        mBinding.setModel(mViewModel);
        mPreferences = mViewModel.getPrefs();
        mBinding.setPrefs(mPreferences);

        mBinding.editAmpMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                int ampMin = Integer.parseInt(mBinding.editAmpMin.getText().toString());
                if (ampMin >= mPreferences.getAmpMax() || ampMin > AMP_THRESH_MIN_VALUE_MAX) {
                    mBinding.editAmpMin.setText(String.valueOf(mPreferences.getAmpMin()));
                } else {
                    mPreferences.setAmpMin(Integer.parseInt(mBinding.editAmpMin.getText().toString()));
                }
            }
        });

        mBinding.editAmpMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                int ampMax = Integer.parseInt(mBinding.editAmpMax.getText().toString());
                if (ampMax > AMP_THRESH_MAX_VALUE_MAX) {
                    mBinding.editAmpMax.setText(String.valueOf(mPreferences.getAmpMax()));
                } else {
                    mPreferences.setAmpMax(Integer.parseInt(mBinding.editAmpMax.getText().toString()));
                }
            }
        });

        mBinding.editFreqBStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                int freqBStart = Integer.parseInt(mBinding.editFreqBStart.getText().toString());
                if (freqBStart > (FREQ_MAX-3)) {
                    mBinding.editFreqBStart.setText(String.valueOf(mPreferences.getFreqBlueStart()));
                } else {
                    mPreferences.setFreqBlueStart(Integer.parseInt(mBinding.editFreqBStart.getText().toString()));
                }
            }
        });

        mBinding.editFreqBEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                int freqBEnd = Integer.parseInt(mBinding.editFreqBEnd.getText().toString());
                if (freqBEnd > (FREQ_MAX-2) || freqBEnd <= mPreferences.getFreqBlueStart() ) {
                    mBinding.editFreqBEnd.setText(String.valueOf(mPreferences.getFreqBlueStart()));
                } else {
                    mPreferences.setFreqBlueEnd(Integer.parseInt(mBinding.editFreqBEnd.getText().toString()));
                }
            }
        });

        mBinding.editFreqGEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                int freqGEnd = Integer.parseInt(mBinding.editFreqGEnd.getText().toString());
                if (freqGEnd > (FREQ_MAX-1) || freqGEnd <= mPreferences.getFreqBlueEnd() ) {
                    mBinding.editFreqGEnd.setText(String.valueOf(mPreferences.getFreqGreenEnd()));
                } else {
                    mPreferences.setFreqGreenEnd(Integer.parseInt(mBinding.editFreqGEnd.getText().toString()));
                }
            }
        });

        mBinding.editFreqREnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                int freqREnd = Integer.parseInt(mBinding.editFreqREnd.getText().toString());
                if (freqREnd > (FREQ_MAX-1) || freqREnd <= mPreferences.getFreqGreenEnd() ) {
                    mBinding.editFreqREnd.setText(String.valueOf(mPreferences.getFreqRedEnd()));
                } else {
                    mPreferences.setFreqRedEnd(Integer.parseInt(mBinding.editFreqREnd.getText().toString()));
                }
            }
        });

        mBinding.editHoldModeInt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                int holdModeInt = Integer.parseInt(mBinding.editHoldModeInt.getText().toString());
                if (holdModeInt > COLOR_INTENSITY_MAX || holdModeInt <=0 ) {
                    mBinding.editHoldModeInt.setText(String.valueOf(mPreferences.getAudioHoldIntensity()));
                } else {
                    mPreferences.setAudioHoldIntensity(Integer.parseInt(mBinding.editHoldModeInt.getText().toString()));
                }
            }
        });
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
