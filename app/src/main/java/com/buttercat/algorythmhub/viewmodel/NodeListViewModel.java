package com.buttercat.algorythmhub.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.buttercat.algorythmhub.BasicApp;
import com.buttercat.algorythmhub.model.NodeRepository;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.view.NodeListFragment;
import com.buttercat.algorythmhub.view.utils.NodeListViewAdapter;

import java.util.List;

/**
 * An {@link AndroidViewModel} for the {@link NodeListFragment}
 */
public class NodeListViewModel extends AndroidViewModel {

    /**
     * An adapter used by the {@link RecyclerView} through
     * databinding, using {@link #getAdapter()}
     */
    private final NodeListViewAdapter myRecyclerViewAdapter;

    private final NodeRepository mRepository;

    /**
     * Constructor for this {@link AndroidViewModel} which has an instance of the {@link NodeRepository}
     * and creates an adapter for the {@link NodeListFragment} {@link RecyclerView}
     *
     * @param application instance used to obtain the {@link NodeRepository}
     *                    and create the {@link NodeListViewAdapter}
     */
    public NodeListViewModel(Application application) {
        super(application);
        mRepository = ((BasicApp) application).getRepository();
        myRecyclerViewAdapter = new NodeListViewAdapter(this);
    }

    /**
     * Method which returns all of the {@link ESP32Node} objects in the network
     *
     * @return an observable list of {@link ESP32Node} objects
     */
    public LiveData<List<ESP32Node>> getNodeListLiveData() {
        return mRepository.getNodesLiveData();
    }

    /**
     * Provides the adapter for databinding
     *
     * @return the {@link NodeListViewAdapter} used in the {@link NodeListFragment}
     */
    public NodeListViewAdapter getAdapter() {
            return myRecyclerViewAdapter;
    }

    public void notifyClickedItem(ESP32Node node) {
        mRepository.notifyClickedItem(node);
    }
}
