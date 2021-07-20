package com.buttercat.algorythmhub.view.utils;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class used to create custom {@link BindingAdapter} to particularize the functionality of databinding
 */
public class CustomViewBindings {
    /**
     * Changes some properties for the {@link RecyclerView}
     * and sets the appropiate {@link RecyclerView.Adapter}
     *
     * @param recyclerView a {@link RecyclerView} which will have it's properties changed
     * @param adapter the {@link androidx.recyclerview.widget.RecyclerView.Adapter} to use
     */
    @BindingAdapter("setAdapter")
    public static void bindRecyclerViewAdapter(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
