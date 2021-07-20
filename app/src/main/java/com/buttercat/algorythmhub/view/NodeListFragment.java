package com.buttercat.algorythmhub.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.buttercat.algorythmhub.viewmodel.NodeListViewModel;
import com.buttercat.algorythmhub.databinding.NodeListFragmentBinding;

/**
 * A custom {@link Fragment} which shows the
 * {@link com.buttercat.algorythmhub.view.utils.NodeListViewAdapter}
 */
public class NodeListFragment extends Fragment {
    /**
     * The databinding class which takes care of inflating the fragment
     */
    private NodeListFragmentBinding mBinding;

    /**
     * Method returning a new instance of this class
     *
     * @return a new {@link NodeListFragment} instance
     */
    /*package*/ static NodeListFragment newInstance() {
        return new NodeListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = NodeListFragmentBinding
                .inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NodeListViewModel mViewModel = new ViewModelProvider(this).get(NodeListViewModel.class);
        mBinding.setLifecycleOwner(this);
        mBinding.setModel(mViewModel);
    }
}
