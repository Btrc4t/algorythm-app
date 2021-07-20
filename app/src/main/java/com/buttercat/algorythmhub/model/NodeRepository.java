package com.buttercat.algorythmhub.model;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.buttercat.algorythmhub.model.definitions.Color;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Mode;
import com.buttercat.algorythmhub.model.definitions.Prefs;
import org.eclipse.californium.core.CoapResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A repository which provides access to nodes discovered on the network
 */
public class NodeRepository {

    private static final String TAG = NodeRepository.class.getSimpleName();
    /**
     * Singleton instance of this class
     */
    private static NodeRepository sInstance;
    private static NodeLookupHelper mNodeLookupHelper;
    private final CoapHelper mCoapHelper;
    private MediatorLiveData<List<ESP32Node>> mLiveNodesList;
    private List<ESP32Node> mNodeList = new ArrayList<>();
    private MutableLiveData<ESP32Node> mLiveNodeClicked;

    /**
     * Default constructor which links the {@link NodeLookupHelper} and {@link CoapHelper}
     *
     * @param nodeLookupHelper a static reference of {@link NodeLookupHelper}
     * @param coapHelper a static reference of {@link CoapHelper}
     */
    private NodeRepository(final NodeLookupHelper nodeLookupHelper, CoapHelper coapHelper) {
        mCoapHelper = coapHelper;
        mNodeLookupHelper = nodeLookupHelper;
        mLiveNodesList = new MediatorLiveData<>();
        mLiveNodeClicked = new MutableLiveData<>();
        mLiveNodesList.addSource(nodeLookupHelper.getNodesLiveData(), esp32Node -> {
            if (esp32Node == null) { // Service lost
                mNodeList = new ArrayList<>();
                mLiveNodesList.postValue(mNodeList);
                return;
            }

            // update coap endpoint values

            CoapResponse modeResponse = mCoapHelper.queryEndpoint(esp32Node, Mode.ENDPOINT);
            if (modeResponse == null) {
                Log.e(TAG, "NodeRepository: null mode endpoint response");
                return;
            }
            Mode mode = Mode.fromInt(modeResponse.getPayload()[0]);
            esp32Node.setMode(mode);
            byte[] rgb = mCoapHelper.queryEndpoint(esp32Node, Color.ENDPOINT).getPayload();
            esp32Node.setColor(new Color(rgb[0], rgb[1], rgb[2]));
            Prefs prefs = getNodePrefs(esp32Node);
            esp32Node.setPrefs(prefs);

            updateNodeInList(esp32Node);
        });

        mNodeLookupHelper.startDiscovery();
    }

    /**
     * @param nodeLookupHelper a singleton {@link NodeLookupHelper} in case the instance must be created
     *
     * @return an instance of this class, {@link NodeRepository}
     */
    public static NodeRepository getInstance(final NodeLookupHelper nodeLookupHelper, final CoapHelper coapHelper) {
        synchronized (NodeRepository.class) {
            if (sInstance == null) {
                sInstance = new NodeRepository(nodeLookupHelper, coapHelper);
            }
        }
        return sInstance;
    }

    /**
     * Observed LiveData will notify the observer when the data has changed.
     *
     * @return a {@link LiveData<List< ESP32Node >>} with the latest contents from the mDNS Lookup Helper
     */
    public LiveData<List<ESP32Node>> getNodesLiveData() {
        return mLiveNodesList;
    }

    public void notifyClickedItem(ESP32Node node) {
        mLiveNodeClicked.postValue(node);
    }

    public LiveData<ESP32Node> getNodeClickedLiveData() {
        return mLiveNodeClicked;
    }

    public ESP32Node getLastSelectedNode() {
        ESP32Node selectedNode;
        selectedNode = mLiveNodeClicked.getValue();
        if (selectedNode == null) {
            // Auto select node if no node is clicked but this method is called with nodes available
            if (!mNodeList.isEmpty()) selectedNode = mNodeList.get(0);
        }
        return selectedNode;
    }

    private Prefs getNodePrefs(ESP32Node node) {
        Prefs prefs = new Prefs();

        int[] preferences = {
                prefs.getAmpMax(),
                prefs.getAmpMin(),
                prefs.getFreqBlueStart(),
                prefs.getFreqBlueEnd(),
                prefs.getFreqGreenEnd(),
                prefs.getFreqRedEnd(),
                prefs.getAudioHoldIntensity()
        };
        CoapResponse coapResponse = mCoapHelper.queryEndpoint(node, Prefs.ENDPOINT);
        if (coapResponse == null) return prefs;
        byte[] prefBytes = coapResponse.getPayload();
        if ((prefBytes.length/2) == preferences.length) {
            for (int b = 0; b < prefBytes.length; b+=2) {
                preferences[b/2] = (((((int) prefBytes[b+1]) & 0xff) << 8)
                        | (((int) prefBytes[b]) & 0xff));
                // Log.i(TAG, "getNodePrefs: b["+(b/2)+"] = "+preferences[b/2]);
            }
        } else {
            Log.e(TAG, "getNodePrefs: got an unexpected size from the node: "+(prefBytes.length/2));
        }
        prefs = new Prefs(preferences);
        return prefs;
    }

    public void updateNodeInList(ESP32Node node) {
        if (mNodeList.isEmpty()) {
            mNodeList.add(node);
            mLiveNodesList.postValue(mNodeList);
            return;
        }
        for (int n = 0; n < mNodeList.size(); n++) { // lists will always be short, is it worth optimizing?
            if (node.getHostName().contentEquals(mNodeList.get(n).getHostName())) {
                mNodeList.set(n, node);
                mLiveNodesList.postValue(mNodeList);
            }
            if (mLiveNodeClicked.getValue() != null && node.getHostName().contentEquals(mLiveNodeClicked.getValue().getHostName())) {
                mLiveNodeClicked.postValue(node);
            }
        }
    }

    public CoapResponse putCoapUpdate(ESP32Node node, String endpoint) {
        return mCoapHelper.putEndpoint(node, endpoint);
    }

    public boolean checkConnection(ESP32Node node) {
            return mCoapHelper.ping(node);
    }
}
