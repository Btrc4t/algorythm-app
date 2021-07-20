package com.buttercat.algorythmhub.model.definitions;

/**
 * The color displayed by a {@link ESP32Node}
 */
public class Color {
    private final byte red;
    private final byte green;
    private final byte blue;
    private final int color;
    public static final String ENDPOINT = "rgb";

    public Color(byte red, byte green, byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        color = android.graphics.Color.rgb(red, green, blue);
    }

    public Color (int color) {
        this.red = (byte) ((color >> 16) & 0xFF);
        this.green = (byte) ((color >> 8) & 0xFF);
        this.blue = (byte) (color & 0xFF);
        this.color = android.graphics.Color.rgb(this.red, this.green, this.blue);
    }

    public byte getRed() {
        return red;
    }

    public byte getGreen() {
        return green;
    }

    public byte getBlue() {
        return blue;
    }

    public int getColor() {
        return color;
    }
}
