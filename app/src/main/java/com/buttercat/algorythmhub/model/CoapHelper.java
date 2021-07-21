package com.buttercat.algorythmhub.model;

import android.content.Context;
import android.util.Log;
import com.buttercat.algorythmhub.model.definitions.Color;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.model.definitions.Mode;
import com.buttercat.algorythmhub.model.definitions.Prefs;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.util.concurrent.*;

import static org.eclipse.californium.core.coap.MediaTypeRegistry.APPLICATION_OCTET_STREAM;

public class CoapHelper {

    /**
     * Singleton instance of this class
     */
    private static CoapHelper sInstance;
    private static final String TAG = CoapHelper.class.getSimpleName();
    private boolean mUsesDtls;
    private CoapClient mCoapClient;

    /**
     * Obtain a singleton {@link CoapHelper}
     *
     * @param executors {@link AppExecutors} used to obtain the executor handling operations
     *
     * @return a singleton instance of the {@link CoapHelper}
     */
    public static CoapHelper getInstance(final AppExecutors executors, boolean useDtls) {
        synchronized (CoapHelper.class) {
            if (sInstance == null) {
                sInstance = new CoapHelper(executors, useDtls);
            }
        }
        return sInstance;
    }

    /**
     *
     *
     * @param executors the {@link java.util.concurrent.Executors} to use to run operations within {@link CoapHelper}
     */
    private CoapHelper(final AppExecutors executors, boolean useDtls) {
        mUsesDtls = useDtls;
        mCoapClient = new CoapClient();
        mCoapClient.setTimeout(1000L);
        mCoapClient.setExecutors(executors.getCoapExecutorService(), executors.getCoapThreadPool(), true);

        if (useDtls) {
            //TODO configure DTLS Client
        }

        Log.d(TAG, "COAP configured");
    }

    public CoapResponse queryEndpoint(ESP32Node node, String endpoint) {

        try {
            String uri = (mUsesDtls ? "coaps" : "coap") + "://" + node.getAddress() + (mUsesDtls ? ":5684" : ":5683") + "/" + endpoint;
            Log.i(TAG, "queryEndpoint: querying "+uri);
            mCoapClient.setURI(uri);
            return mCoapClient.get();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return null;
        }
    }

    public CoapResponse putEndpoint(ESP32Node node, String endpoint) {

        try {
            String uri = (mUsesDtls ? "coaps" : "coap") + "://" + node.getAddress() + (mUsesDtls ? ":5684" : ":5683") + "/" + endpoint;
            Log.i(TAG, "putEndpoint: posting " + uri);
            mCoapClient.setURI(uri);

            if (endpoint.contentEquals(Mode.ENDPOINT)) {
                return mCoapClient.put(new byte[] {(byte) (node.getMode().getModeInt() & 0xFF)}, APPLICATION_OCTET_STREAM);
            } else if (endpoint.contentEquals(Color.ENDPOINT)) {
                return mCoapClient.put(new byte[] {
                        node.getColor().getRed(),
                        node.getColor().getGreen(),
                        node.getColor().getBlue()
                }, APPLICATION_OCTET_STREAM);
            } else if (endpoint.contentEquals(Prefs.ENDPOINT)) {
                byte[] prefBytes = {
                        (byte) (node.getPrefs().getAmpMin() & 0xFF),
                        (byte) (node.getPrefs().getAmpMin() >> 8 & 0xFF),
                        (byte) (node.getPrefs().getAmpMax() & 0xFF),
                        (byte) (node.getPrefs().getAmpMax() >> 8 & 0xFF),
                        (byte) (node.getPrefs().getFreqBlueStart() & 0xFF),
                        (byte) (node.getPrefs().getFreqBlueStart() >> 8 & 0xFF),
                        (byte) (node.getPrefs().getFreqBlueEnd() & 0xFF),
                        (byte) (node.getPrefs().getFreqBlueEnd() >> 8 & 0xFF),
                        (byte) (node.getPrefs().getFreqGreenEnd() & 0xFF),
                        (byte) (node.getPrefs().getFreqGreenEnd() >> 8 & 0xFF),
                        (byte) (node.getPrefs().getFreqRedEnd() & 0xFF),
                        (byte) (node.getPrefs().getFreqRedEnd() >> 8 & 0xFF),
                        (byte) (node.getPrefs().getAudioHoldIntensity() & 0xFF),
                        (byte) (node.getPrefs().getAudioHoldIntensity() >> 8 & 0xFF),
                };
                return mCoapClient.put(prefBytes, APPLICATION_OCTET_STREAM);
            } else {
                Log.e(TAG, "putEndpoint: invalid endpoint: " + endpoint);
                return null;
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return null;
        }

    }

    public Boolean ping(ESP32Node node) {
        try {
            String uri = (mUsesDtls ? "coaps" : "coap") + "://" + node.getAddress() + (mUsesDtls ? ":5684" : ":5683") + "/";
            Log.i(TAG, "ping: " + uri);
            mCoapClient.setURI(uri);
            return mCoapClient.ping();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return false;
        }
    }
}