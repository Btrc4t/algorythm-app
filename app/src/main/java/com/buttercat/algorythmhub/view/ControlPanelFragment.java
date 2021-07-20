package com.buttercat.algorythmhub.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.buttercat.algorythmhub.R;
import com.buttercat.algorythmhub.databinding.ControlPanelFragmentBinding;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Mode;
import com.buttercat.algorythmhub.viewmodel.ControlPanelViewModel;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.OnColorSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A custom {@link Fragment} which shows the
 * {@link com.buttercat.algorythmhub.view.utils.NodeListViewAdapter}
 */
public class ControlPanelFragment extends Fragment {
    private static final String TAG = ControlPanelFragment.class.getSimpleName();
    /**
     * The databinding class which takes care of inflating the fragment
     */
    private ControlPanelFragmentBinding mControlPanelFragmentBinding;
    private ControlPanelViewModel mViewModel;

    /**
     * Method returning a new instance of this class
     *
     * @return a new {@link ControlPanelFragment} instance
     */
    /*package*/ static ControlPanelFragment newInstance() {
        return new ControlPanelFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mControlPanelFragmentBinding = ControlPanelFragmentBinding
                .inflate(inflater, container, false);
        return mControlPanelFragmentBinding.getRoot();
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
        Log.d("TAG", "onViewCreated: ControlPanelFragment ");
        mViewModel = new ViewModelProvider(this).get(ControlPanelViewModel.class);
        mControlPanelFragmentBinding.setLifecycleOwner(this);
        // mControlPanelFragmentBinding.setModel(mViewModel);
        // Create an ArrayAdapter using the string array and a default spinner layout
        List<String> modes = new ArrayList<>();
        for (Mode mode: Mode.values()) {
            modes.add(mode.name().replace('_',' '));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, modes);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mControlPanelFragmentBinding.modeSpinner.setAdapter(adapter);
        updateData();
        mControlPanelFragmentBinding.modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Mode newMode = Mode.fromInt(position);
                mViewModel.updateSelectedNodeMode(newMode);
                if (newMode == Mode.MANUAL) updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "onNothingSelected");
            }
        });

        ((HSLColorPicker)mControlPanelFragmentBinding.colorPicker).setColorSelectionListener(new OnColorSelectionListener() {
            @Override
            public void onColorSelected(int i) { }

            @Override
            public void onColorSelectionStart(int i) { }

            @Override
            public void onColorSelectionEnd(int i) {
                mViewModel.updateSelectedNodeColor(i);
            }
        });
    }

    private void updateData() {
        ESP32Node selectedNode = mViewModel.getSelectedNode();
        if (selectedNode == null) {
            return;
        }
        if (mViewModel.checkConnection(selectedNode)) {
            mControlPanelFragmentBinding.modeSpinner.setSelection(selectedNode.getMode().getModeInt());
            mControlPanelFragmentBinding.headingTitle.setText(selectedNode.getRoom());

            if (selectedNode.getMode() == Mode.MANUAL || selectedNode.getMode() == Mode.AUDIO_INTENSITY)
                ((HSLColorPicker)mControlPanelFragmentBinding.colorPicker).setColor(selectedNode.getColor().getColor());
        } else {
            mControlPanelFragmentBinding.headingTitle.setText(R.string.no_node_selected);
        }
    }
}
