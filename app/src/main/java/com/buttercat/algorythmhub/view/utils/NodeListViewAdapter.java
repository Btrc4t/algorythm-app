package com.buttercat.algorythmhub.view.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.buttercat.algorythmhub.databinding.ItemRowBinding;
import com.buttercat.algorythmhub.model.definitions.ESP32Node;
import com.buttercat.algorythmhub.viewmodel.NodeListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom {@link RecyclerView.Adapter} which is purpose-built to show {@link ESP32Node} objects
 */
public class NodeListViewAdapter
        extends RecyclerView.Adapter<NodeListViewAdapter.NodeItemViewHolder>
        implements NodeClickListener {

    /**
     * The {@link androidx.lifecycle.AndroidViewModel} used to obtain a list of {@link ESP32Node}
     */
    private NodeListViewModel mainViewModel;
    /**
     * The list of {@link com.buttercat.algorythmhub.model.definitions.ESP32Node} objects to be shown in this adapter
     */
    private List<ESP32Node> mNodeListItems = new ArrayList<>();

    /**
     * Constructor which creates an {@link java.util.Observer} for the list of {@link ESP32Node}
     *
     * @param viewModel a reference for the {@link androidx.lifecycle.AndroidViewModel} which provides
     *                  the list of {@link ESP32Node} objects
     */
    public NodeListViewAdapter(NodeListViewModel viewModel) {
        this.mainViewModel = viewModel;
        mainViewModel.getNodeListLiveData().observe(ProcessLifecycleOwner.get(), newNodeItems -> {
            mNodeListItems = newNodeItems;
            this.notifyDataSetChanged(); //TODO use DiffUtils
        });

    }

    @NonNull
    @Override
    public NodeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRowBinding binding = ItemRowBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NodeItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NodeItemViewHolder holder, int position) {
        ESP32Node esp32Node = mNodeListItems.get(position);
        if (esp32Node == null) return;
        holder.bind(esp32Node);
        holder.itemRowBinding.setItemClickListener(this);
    }


    @Override
    public int getItemCount() {
        return mNodeListItems.size();
    }

    /*package*/ class NodeItemViewHolder extends RecyclerView.ViewHolder {
        /*package*/ ItemRowBinding itemRowBinding;

        /*package*/ NodeItemViewHolder(ItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }

        /*package*/ void bind(ESP32Node node) {
            itemRowBinding.setItem(node);
            itemRowBinding.executePendingBindings();
        }
    }

    /**
     * Method called when a {@link ESP32Node} is clicked, showing a {@link Toast} as feedback
     * to the user
     *
     * @param node the {@link ESP32Node} which was clicked
     */
    public void nodeItemClicked(ESP32Node node) {
        mainViewModel.notifyClickedItem(node);
    }
}

