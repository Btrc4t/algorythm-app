package com.buttercat.algorythmhub.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;
import com.buttercat.algorythmhub.BasicApp;
import com.buttercat.algorythmhub.model.NodeRepository;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Prefs;
import com.buttercat.algorythmhub.view.NodeListFragment;
import com.buttercat.algorythmhub.view.utils.NodeListViewAdapter;
import org.eclipse.californium.core.CoapResponse;
import timber.log.Timber;

/**
 * An {@link AndroidViewModel} for the {@link NodeListFragment}
 */
public class PreferencesViewModel extends AndroidViewModel {

    private final NodeRepository mRepository;

    /**
     * Constructor for this {@link AndroidViewModel} which has an instance of the {@link NodeRepository}
     * and creates an adapter for the {@link NodeListFragment} {@link RecyclerView}
     *
     * @param application instance used to obtain the {@link NodeRepository}
     *                    and create the {@link NodeListViewAdapter}
     */
    public PreferencesViewModel(Application application) {
        super(application);
        mRepository = ((BasicApp) application).getRepository();
    }

    public ESP32Node getSelectedNode() {
        return mRepository.getLastSelectedNode();
    }

    private void checkCoapResponseAndUpdate(ESP32Node node, CoapResponse response) {
        if (response != null && response.isSuccess()) {
            mRepository.updateNodeInList(node);
        } else {
            Timber.e("updateSelectedNodeMode: null CoapResponse or other failure");
        }
    }

    public boolean checkConnection(ESP32Node node) {
        return mRepository.checkConnection(node);
    }

    public void updatePreferences(Prefs prefs) {
        ESP32Node node = getSelectedNode();
        if (node == null) return;
        node.setPrefs(prefs);

        CoapResponse coapResponse = mRepository.putCoapUpdate(node, Prefs.ENDPOINT);
        checkCoapResponseAndUpdate(node, coapResponse);
    }

    public Prefs getPrefs() {
        ESP32Node node = getSelectedNode();
        if (node == null || node.getPrefs() == null) return new Prefs();
        return node.getPrefs();
    }
}
