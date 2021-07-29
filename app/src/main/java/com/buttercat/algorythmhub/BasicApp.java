package com.buttercat.algorythmhub;

import android.app.Application;
import com.buttercat.algorythmhub.model.AppExecutors;
import com.buttercat.algorythmhub.model.CoapHelper;
import com.buttercat.algorythmhub.model.NodeRepository;
import com.buttercat.algorythmhub.model.NodeLookupHelper;
import timber.log.Timber;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BasicApp extends Application {

    /**
     * {@link java.util.concurrent.Executor} objects to be used for the whole application
     */
    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        Timber.plant(new Timber.DebugTree());
    }

    /**
     * Getter for the singleton {@link NodeLookupHelper}
     *
     * @return the singleton {@link NodeLookupHelper}
     */
    public NodeLookupHelper getNodeLookupHelper() {
        return NodeLookupHelper.getInstance(this, mAppExecutors);
    }

    /**
     * Getter for the singleton {@link CoapHelper}
     *
     * @return the singleton {@link CoapHelper}
     */
    public CoapHelper getCoapHelper() {
        return CoapHelper.getInstance(mAppExecutors, false);
    }

    /**
     * Getter for the singleton {@link NodeRepository}
     *
     * @return the singleton {@link NodeRepository}
     */
    public NodeRepository getRepository() {
        return NodeRepository.getInstance(getNodeLookupHelper(), getCoapHelper());
    }
}

