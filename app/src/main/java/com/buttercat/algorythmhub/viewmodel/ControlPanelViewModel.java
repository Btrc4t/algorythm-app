package com.buttercat.algorythmhub.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;
import com.buttercat.algorythmhub.BasicApp;
import com.buttercat.algorythmhub.model.NodeRepository;
import com.buttercat.algorythmhub.model.definitions.Color;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Mode;
import com.buttercat.algorythmhub.view.NodeListFragment;
import com.buttercat.algorythmhub.view.utils.NodeListViewAdapter;
import org.eclipse.californium.core.CoapResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An {@link AndroidViewModel} for the {@link NodeListFragment}
 */
public class ControlPanelViewModel extends AndroidViewModel {

    private static final String TAG = ControlPanelViewModel.class.getSimpleName();
    private NodeRepository mRepository;

    /**
     * Constructor for this {@link AndroidViewModel} which has an instance of the {@link NodeRepository}
     * and creates an adapter for the {@link NodeListFragment} {@link RecyclerView}
     *
     * @param application instance used to obtain the {@link NodeRepository}
     *                    and create the {@link NodeListViewAdapter}
     */
    public ControlPanelViewModel(Application application) {
        super(application);
        mRepository = ((BasicApp) application).getRepository();
    }

    public ESP32Node getSelectedNode() {
        return mRepository.getLastSelectedNode();
    }

    public void updateSelectedNodeMode(Mode mode) {
        ESP32Node node = getSelectedNode();
        if (node == null) return;
        node.setMode(mode);

        CoapResponse coapResponse = mRepository.putCoapUpdate(node, Mode.ENDPOINT);
        checkCoapResponseAndUpdate(node, coapResponse);
    }

    public void updateSelectedNodeColor(int i) {
        ESP32Node node = getSelectedNode();
        if (node == null) return;
        // Cannot set RGB value when not in manual or audio_intensity modes
        if (node.getMode() != Mode.MANUAL && node.getMode() != Mode.AUDIO_INTENSITY) return;
        Color color = new Color(i);
        node.setColor(color);

        CoapResponse coapResponse = mRepository.putCoapUpdate(node, Color.ENDPOINT);
        checkCoapResponseAndUpdate(node, coapResponse);
    }

    private void checkCoapResponseAndUpdate(ESP32Node node, CoapResponse response) {
        if (response != null && response.isSuccess()) {
            mRepository.updateNodeInList(node);
        } else {
            Log.e(TAG, "updateSelectedNodeMode: null CoapResponse or other failure");
        }
    }

    public boolean checkConnection(ESP32Node node) {
        return mRepository.checkConnection(node);
    }
}
