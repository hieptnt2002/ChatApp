package com.realtime.message.view.fragment.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.realtime.message.R;
import com.realtime.message.databinding.FragmentDirectoryBinding;
import com.realtime.message.model.User;
import com.realtime.message.view.adapter.UserDirectoryAdapter;
import com.realtime.message.viewmodel.DirectoryViewModel;

import java.util.List;

public class DirectoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    FragmentDirectoryBinding directoryBinding;
    UserDirectoryAdapter directoryAdapter;
    RecyclerView rvDirectory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        directoryBinding = FragmentDirectoryBinding.inflate(inflater, container, false);
        directoryBinding.shimmerEffect.startShimmerAnimation();
        rvDirectory = directoryBinding.rvUsrMsg;
        rvDirectory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvDirectory.setNestedScrollingEnabled(false);
        DirectoryViewModel directoryViewModel = new DirectoryViewModel(getActivity());
        directoryBinding.setDirectoryViewModel(directoryViewModel);
        directoryBinding.getDirectoryViewModel().getUserDirectoryFromRealtimeDB();
        directoryBinding.getDirectoryViewModel().getUserMutableLiveData().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                setAdapterRv(users);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        directoryBinding.shimmerEffect.stopShimmerAnimation();

                    }
                }, 1000);
            }
        });

        directoryBinding.layoutRefresh.setOnRefreshListener(this);
        return directoryBinding.getRoot();
    }

    private void setAdapterRv(List<User> mList) {
        directoryAdapter = new UserDirectoryAdapter(getActivity(), mList);
        rvDirectory.setAdapter(directoryAdapter);
        if (directoryAdapter != null) {
            int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_user);
            int numItems = 0;
            numItems = directoryAdapter.getItemCount(); // số lượng item trong RecyclerView
            int totalHeight = itemHeight * numItems; // tổng chiều cao của tất cả các item trong RecyclerView
            ViewGroup.LayoutParams params = rvDirectory.getLayoutParams();
            params.height = totalHeight;
            rvDirectory.setLayoutParams(params);//

        }

    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Refresh data", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                directoryBinding.layoutRefresh.setRefreshing(false);
            }
        },2000);
    }
}
