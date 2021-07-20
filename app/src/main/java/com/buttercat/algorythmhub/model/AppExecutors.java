package com.buttercat.algorythmhub.model;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Executors for the app
 */
public class AppExecutors {
    /**
     * {@link ExecutorService} for net operations
     */
    private final ExecutorService mNetworkIO;
    /**
     * {@link ExecutorService} for net operations
     */
    private final ExecutorService mCoapIO;
    /**
     * {@link ScheduledThreadPoolExecutor} for CoAP network operations
     */
    private final ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor;

    /**
     * Private constructor initializing the {@link ExecutorService}
     *
     * @param netIO an {@link Executor} for net operations
     */
    private AppExecutors(ExecutorService netIO, ExecutorService coapIO, ScheduledThreadPoolExecutor mCoapPool) {
        this.mNetworkIO = netIO;
        this.mCoapIO = coapIO;
        this.mScheduledThreadPoolExecutor = mCoapPool;
    }

    /**
     * Constructor which creates a single thread {@link ExecutorService} for net operations
     */
    public AppExecutors() {
        this(
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor(),
                (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(4)
        );
    }

    /**
     * Getter for the net operations {@link ExecutorService}
     *
     * @return net operations {@link ExecutorService}
     */
    public ExecutorService getNetExecutorService() {
        return mNetworkIO;
    }

    /**
     * Getter for the CoAP operations {@link ExecutorService}
     *
     * @return CoAP operations {@link ExecutorService}
     */
    public ExecutorService getCoapExecutorService() {
        return mCoapIO;
    }

    /**
     * Getter for the CoAP operations {@link ScheduledThreadPoolExecutor}
     *
     * @return CoAP operations {@link ScheduledThreadPoolExecutor}
     */
    public ScheduledThreadPoolExecutor getCoapThreadPool() {
        return mScheduledThreadPoolExecutor;
    }

}