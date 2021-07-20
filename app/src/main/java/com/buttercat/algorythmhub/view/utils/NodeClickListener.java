package com.buttercat.algorythmhub.view.utils;

import com.buttercat.algorythmhub.model.definitions.ESP32Node;

/**
 * Interface used by the {@link NodeListViewAdapter} to receive a click event on one of the {@link ESP32Node}
 */
public interface NodeClickListener {
    /**
     * Method called when a {@link ESP32Node} is clicked
     *
     * @param node the {@link ESP32Node} which was clicked
     */
    void nodeItemClicked(ESP32Node node);
}
