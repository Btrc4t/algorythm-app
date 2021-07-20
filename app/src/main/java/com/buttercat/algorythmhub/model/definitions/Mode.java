package com.buttercat.algorythmhub.model.definitions;

/**
 * The {@link ESP32Node} functioning mode
 */
public enum Mode {


    /**
     * Has one color set manually
     */
    MANUAL(0),
    /**
     * Color intensity varies with audio amplitude
     */
    AUDIO_INTENSITY(1),
    /**
     * Color changes with audio frequencies
     */
    AUDIO_FREQ(2),
    /**
     * Color changes with audio frequencies and intensity varies with audio amplitude
     */
    AUDIO(3),
    /**
     * Leds will not light up
     */
    OFF(4),
    /**
     * Color changes with audio frequencies and intensity varies with audio amplitude
     * When amplitude is under the minimum, the last color set is shown.
     */
    AUDIO_HOLD(5);
    /**
     * Field containing the value of this enum
     */
    private int type;
    public static final String ENDPOINT = "mode";

    /**
     * Creates an object using an input value
     *
     * @param value to be associated with an item type
     */
    Mode(int value) {
        this.type = value;
    }

    /**
     * Returns an object created from an input value.
     *
     * @param value to be associated with an item type
     * @return object for the input value
     */
    public static Mode fromInt(int value) {
        for (Mode item : Mode.values()) {
            if (item.type == value) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns the value associated with the item
     *
     * @return value associated with the item
     */
    public int getModeInt() {
        return type;
    }
}
