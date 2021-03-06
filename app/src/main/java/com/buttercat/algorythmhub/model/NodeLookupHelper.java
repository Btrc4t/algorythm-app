package com.buttercat.algorythmhub.model;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Mode;
import com.buttercat.algorythmhub.model.definitions.Prefs;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class NodeLookupHelper implements LifecycleOwner {

    /**
     * Singleton instance of this class
     */
    private static NodeLookupHelper sInstance;
    private static final String SERVICE_TYPE = "_http._tcp.";
    private static final String SERVICE_NAME = "ALGORYTHM";
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    MutableLiveData<Boolean> resolverAvailable = new MutableLiveData<>();
    Queue<NsdServiceInfo> servicesToResolveQueue = new ConcurrentLinkedQueue<NsdServiceInfo>();
    private NsdManager mNsdManager;
    MutableLiveData<ESP32Node> mNodesLiveData;
    private final Executor executor;

    /**
     * Obtain a singleton {@link NodeLookupHelper}
     *
     * @param appContext a {@link Context} used to obtain a {@link NodeLookupHelper} instance
     * @param executors {@link AppExecutors} used to obtain the executor handling operations
     *
     * @return a singleton instance of the {@link NodeLookupHelper}
     */
    public static NodeLookupHelper getInstance(final Context appContext, final AppExecutors executors) {
        synchronized (NodeLookupHelper.class) {
            if (sInstance == null) {
                sInstance = new NodeLookupHelper(appContext, executors);
            }
        }
        return sInstance;
    }

    /**
     *
     *
     * @param appContext a {@link Context} used to obtain a {@link NodeLookupHelper} instance
     * @param executors the {@link java.util.concurrent.Executors} to use to run operations within {@link NodeLookupHelper}
     */
    private NodeLookupHelper(final Context appContext,
                             final AppExecutors executors) {
        mNsdManager = (NsdManager) appContext.getSystemService(Context.NSD_SERVICE);
        mNodesLiveData = new MutableLiveData<>();
        executor = executors.getNetExecutorService();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                resolverAvailable.postValue(true);
                Timber.d("onResolveFailed");
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {

                Timber.d("onServiceResolved: Host: %s", serviceInfo.getHost());
                Timber.d("onServiceResolved: Hostname: %s", serviceInfo.getHost().getHostName());
                Timber.d("onServiceResolved: Canonical Hostname: %s", serviceInfo.getHost().getCanonicalHostName());
                Timber.d("onServiceResolved: Service Name: %s", serviceInfo.getServiceName());
                Timber.d("onServiceResolved: Service Type: %s", serviceInfo.getServiceType());
                if (!serviceInfo.getServiceName().startsWith(SERVICE_NAME)) {
                    Timber.d("Found unrelated service: %s", serviceInfo.getServiceName());
                    resolverAvailable.postValue(true);
                    return;
                }
                String room = "Undefined";
                Mode mode = Mode.OFF;
                for (Map.Entry<String, byte[]> entry: serviceInfo.getAttributes().entrySet()) {
                    if (entry.getKey().contentEquals("room")) {
                        room = new String(entry.getValue(), StandardCharsets.UTF_8);
                    } else if (entry.getKey().contentEquals("mode")) {
                        mode = Mode.fromInt(entry.getValue()[0]);
                    }
                }
                ESP32Node esp32Node = new ESP32Node(serviceInfo.getHost().getHostName(), serviceInfo.getHost().getHostAddress(),room,mode, new Prefs(),null);
                mNodesLiveData.postValue(esp32Node);
                resolverAvailable.postValue(true);
            }
        };

        resolverAvailable.observe(this, available -> {
            if (available == null) {
                Timber.e("null resolver input");
                return;
            }
            Timber.d("available is "+available);
            if (available) {
                Timber.d("Resolver now available");
                NsdServiceInfo service = servicesToResolveQueue.poll();
                if (service != null) {
                    Timber.d("Service available to resolve");
                    resolverAvailable.setValue(false);
                    mNsdManager.resolveService(service, mResolveListener);
                } else {
                    Timber.d("Queue consumed = No service available to resolve");
                }

            } else {
                Timber.d("Resolver now unavailable");
            }
        });
        resolverAvailable.setValue(true);
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Timber.d("Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! resolve it!
                Timber.d("Service discovery success: %s", service);
                if (service.getServiceType().equals(SERVICE_TYPE)) {
                    servicesToResolveQueue.add(service);
                    resolverAvailable.postValue(resolverAvailable.getValue());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Timber.e("service lost: %s", service);
                mNodesLiveData.postValue(null);
                resolverAvailable.postValue(resolverAvailable.getValue());
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Timber.i("Discovery stopped: %s; Restarting.", serviceType);
                restartDiscovery();
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Timber.e("Discovery failed: Error code:%s", errorCode);
                restartDiscovery();
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Timber.e("Discovery failed: Error code:%s", errorCode);
                restartDiscovery();
            }
        };
    }

    public void restartDiscovery() {
        Timber.d("restartDiscovery:");
        stopDiscovery();
        startDiscovery();
    }

    public void startDiscovery() {
        Timber.d("startDiscovery:");
        executor.execute(() -> mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener));
    }

    public void stopDiscovery() {
        Timber.d("stopDiscovery: ");
        executor.execute(() -> mNsdManager.stopServiceDiscovery(mDiscoveryListener));
    }

    public LiveData<ESP32Node> getNodesLiveData() {
        return mNodesLiveData;
    }

    @NonNull
    @NotNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}