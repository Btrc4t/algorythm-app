package com.buttercat.algorythmhub.model.definitions;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * A node present in the user's house, discovered through mDNS
 */
public class ESP32Node implements Parcelable {

    /**
     * The hostname of the node
     */
    private String hostName;
    /**
     * The IP address of the node
     */
    private final String address;
    /**
     * The room name set for the node
     */
    private String room;
    /**
     * The {@link Mode}
     */
    private Mode mode;
    /**
     * The {@link Prefs}
     */
    private Prefs prefs;
    /**
     * The {@link Color}
     */
    private Color color;

    public static final Creator<ESP32Node> CREATOR = new Creator<ESP32Node>() {
        @Override
        public ESP32Node createFromParcel(Parcel in) {
            return new ESP32Node(in);
        }

        @Override
        public ESP32Node[] newArray(int size) {
            return new ESP32Node[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hostName);
        dest.writeString(this.address);
        dest.writeString(this.room);
        dest.writeInt(this.mode.getModeInt());
        dest.writeInt(this.color.getColor());
        dest.writeParcelable(this.prefs, 0);
    }

    public ESP32Node(Parcel in){
        this.hostName = in.readString();
        this.address = in.readString();
        this.room = in.readString();
        this.mode = Mode.fromInt(in.readInt());
        this.color = new Color(in.readInt());
        this.prefs = in.readParcelable(Prefs.class.getClassLoader());
    }

    public ESP32Node(String hostName, String address, String room, Mode mode, Prefs prefs, Color color) {
        this.hostName = hostName;
        this.address = address;
        this.room = room;
        this.mode = mode;
        this.prefs = prefs;
        this.color = color;
    }

    public String getHostName() {
        return hostName;
    }

    public String getRoom() {
        return room;
    }

    public Mode getMode() {
        return mode;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Prefs getPrefs() {
        return prefs;
    }

    public void setPrefs(Prefs prefs) {
        this.prefs = prefs;
    }

    public String getAddress() {
        return address;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ESP32Node esp32Node = (ESP32Node) o;
        return getHostName().equals(esp32Node.getHostName()) && getAddress().equals(esp32Node.getAddress()) && Objects.equals(getRoom(), esp32Node.getRoom()) && getMode() == esp32Node.getMode() && Objects.equals(getPrefs(), esp32Node.getPrefs()) && Objects.equals(getColor(), esp32Node.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHostName(), getAddress(), getRoom(), getMode(), getPrefs(), getColor());
    }
}
