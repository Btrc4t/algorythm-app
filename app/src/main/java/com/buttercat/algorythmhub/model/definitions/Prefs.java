package com.buttercat.algorythmhub.model.definitions;

import android.os.Parcel;
import android.os.Parcelable;
import timber.log.Timber;

/**
 * Preferences for the {@link ESP32Node}
 */
public class Prefs implements Parcelable {
    private int mAmpMin = 350;
    private int mAmpMax = 1650;
    private int mFreqBlueStart = 300;
    private int mFreqBlueEnd = 500;
    private int mFreqGreenEnd = 1100;
    private int mFreqRedEnd = 3500;
    private int mAudioHoldIntensity = 45;
    public static final String ENDPOINT = "prefs";

    protected Prefs(Parcel in) {
        this.mAmpMin = in.readInt();
        this.mAmpMax = in.readInt();
        this.mFreqBlueStart = in.readInt();
        this.mFreqBlueEnd = in.readInt();
        this.mFreqGreenEnd = in.readInt();
        this.mFreqRedEnd = in.readInt();
    }

    public Prefs() { /* init with defaults */ }

    public Prefs(int[] prefs) { // these are the defaults
        if (prefs.length != 7) {
            Timber.e("Prefs: Invalid array length: " +prefs.length);
            return;
        }
        this.mAmpMin = prefs[0];
        this.mAmpMax = prefs[1];
        this.mFreqBlueStart = prefs[2];
        this.mFreqBlueEnd = prefs[3];
        this.mFreqGreenEnd = prefs[4];
        this.mFreqRedEnd = prefs[5];
        this.mAudioHoldIntensity = prefs[6];
    }

    public static final Creator<Prefs> CREATOR = new Creator<Prefs>() {
        @Override
        public Prefs createFromParcel(Parcel in) {
            return new Prefs(in);
        }

        @Override
        public Prefs[] newArray(int size) {
            return new Prefs[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mAmpMin);
        dest.writeInt(mAmpMax);
        dest.writeInt(mFreqBlueStart);
        dest.writeInt(mFreqBlueEnd);
        dest.writeInt(mFreqGreenEnd);
        dest.writeInt(mFreqRedEnd);
    }

    public int getAmpMin() {
        return mAmpMin;
    }

    public void setAmpMin(int amp_min) {
        this.mAmpMin = amp_min;
    }

    public int getAmpMax() {
        return mAmpMax;
    }

    public void setAmpMax(int amp_max) {
        this.mAmpMax = amp_max;
    }

    public int getFreqBlueStart() {
        return mFreqBlueStart;
    }

    public void setFreqBlueStart(int freq_b_start) {
        this.mFreqBlueStart = freq_b_start;
    }

    public int getFreqBlueEnd() {
        return mFreqBlueEnd;
    }

    public void setFreqBlueEnd(int freq_b_end) {
        this.mFreqBlueEnd = freq_b_end;
    }

    public int getFreqGreenEnd() {
        return mFreqGreenEnd;
    }

    public void setFreqGreenEnd(int freq_g_end) {
        this.mFreqGreenEnd = freq_g_end;
    }

    public int getFreqRedEnd() {
        return mFreqRedEnd;
    }

    public void setFreqRedEnd(int freq_r_end) {
        this.mFreqRedEnd = freq_r_end;
    }

    public int getAudioHoldIntensity() {
        return mAudioHoldIntensity;
    }

    public void setAudioHoldIntensity(int mAudioHoldIntensity) {
        this.mAudioHoldIntensity = mAudioHoldIntensity;
    }
}
