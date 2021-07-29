package com.buttercat.algorythmhub.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.buttercat.algorythmhub.BasicApp;
import com.buttercat.algorythmhub.model.NodeRepository;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.view.MainActivity;

/**
 * An {@link AndroidViewModel} for the {@link MainActivity}
 */
public class MainActivityViewModel extends AndroidViewModel {

    private final NodeRepository mRepository;

    /**
     * Constructor for this {@link AndroidViewModel} which has an instance of the {@link NodeRepository}
     * and creates an adapter for the {@link com.buttercat.algorythmhub.view.NodeListFragment} {@link RecyclerView}
     *
     * @param application instance used to obtain the {@link NodeRepository}
     *                    and create the {@link com.buttercat.algorythmhub.view.utils.NodeListViewAdapter}
     */
    public MainActivityViewModel(Application application) {
        super(application);
        mRepository = ((BasicApp) application).getRepository();
    }

    public LiveData<ESP32Node> getSelectedNodeLiveData() {
        return mRepository.getNodeClickedLiveData();
    }
}
