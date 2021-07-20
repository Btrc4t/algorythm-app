package com.buttercat.algorythmhub.model;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Mode;
import com.buttercat.algorythmhub.model.definitions.Prefs;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executor;

public class NodeLookupHelper {

    /**
     * Singleton instance of this class
     */
    private static NodeLookupHelper sInstance;
    private static final String TAG = NodeLookupHelper.class.getSimpleName();
    private static final String SERVICE_TYPE = "_http._tcp.";
    private static final String SERVICE_NAME = "ALGORYTHM";
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;
    private NsdManager nsdManager;
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
        nsdManager = (NsdManager) appContext.getSystemService(Context.NSD_SERVICE);
        mNodesLiveData = new MutableLiveData<>();
        executor = executors.getNetExecutorService();
        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "onResolveFailed");
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                
                Log.d(TAG, "onServiceResolved: Host: " + serviceInfo.getHost());
                Log.d(TAG, "onServiceResolved: Hostname: " + serviceInfo.getHost().getHostName());
                Log.d(TAG, "onServiceResolved: Canonical Hostname: " + serviceInfo.getHost().getCanonicalHostName());
                Log.d(TAG, "onServiceResolved: Service Name: " + serviceInfo.getServiceName());
                Log.d(TAG, "onServiceResolved: Service Type: " + serviceInfo.getServiceType());
                if (!serviceInfo.getServiceName().contentEquals(SERVICE_NAME)) {
                    Log.d(TAG, "Found unrelated service: " + serviceInfo.getServiceName());
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
            }
        };

        discoveryListener = new NsdManager.DiscoveryListener() {
            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success: " + service);
                if (service.getServiceType().equals(SERVICE_TYPE)) {
                    nsdManager.resolveService(service, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
                mNodesLiveData.postValue(null);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                stopDiscovery();
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                stopDiscovery();
            }
        };
    }

    public void startDiscovery() {
        Log.d(TAG, "startDiscovery:");
        executor.execute(() -> nsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener));
    }

    public void stopDiscovery() {
        Log.d(TAG, "stopDiscovery: ");
        executor.execute(() -> nsdManager.stopServiceDiscovery(discoveryListener));
    }

    public LiveData<ESP32Node> getNodesLiveData() {
        return mNodesLiveData;
    }
}