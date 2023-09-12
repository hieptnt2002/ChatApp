package com.realtime.message.view.fragment.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.realtime.message.R;
import com.realtime.message.databinding.FragmentMsgBinding;
import com.realtime.message.model.User;
import com.realtime.message.view.adapter.UserMsgAdapter;
import com.realtime.message.viewmodel.UserMsgViewModel;

import java.util.ArrayList;
import java.util.List;


public class MsgFragment extends Fragment {
    FragmentMsgBinding msgBinding;
    List<User> mList;
    UserMsgAdapter userItemAdapter, userItemHrzAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        msgBinding = FragmentMsgBinding.inflate(inflater, container, false);
        msgBinding.shimmer.startShimmerAnimation();

        msgBinding.rvUsrMsg.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        msgBinding.rvUsrOnline.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        msgBinding.rvUsrMsg.setNestedScrollingEnabled(false);
        mList = new ArrayList<>();
//        UserMsgViewModel userViewModel = new ViewModelProvider(this).get(UserMsgViewModel.class);
        UserMsgViewModel userViewModel = new UserMsgViewModel(getActivity());
        msgBinding.setUserMsgModel(userViewModel);
        msgBinding.getUserMsgModel().getUserMutableLiveData().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> lUserModels) {
                mList = lUserModels;
                setAdapterRv();
                msgBinding.shimmer.stopShimmerAnimation();
            }
        });
        msgBinding.getUserMsgModel().getUserChat();

        return msgBinding.getRoot();
    }

    private void setAdapterRv() {
        userItemAdapter = new UserMsgAdapter(getActivity(), mList, 0);

        msgBinding.rvUsrMsg.setAdapter(userItemAdapter);

        userItemHrzAdapter = new UserMsgAdapter(getActivity(), mList, 1);
        msgBinding.rvUsrOnline.setAdapter(userItemHrzAdapter);
        // Tính toán tổng chiều cao của các item

        if (userItemAdapter != null) {
            int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_user);
            int numItems = 0;
            numItems = userItemAdapter.getItemCount(); // số lượng item trong RecyclerView
            int totalHeight = itemHeight * numItems; // tổng chiều cao của tất cả các item trong RecyclerView
            ViewGroup.LayoutParams params = msgBinding.rvUsrMsg.getLayoutParams();
            params.height = totalHeight;
            msgBinding.rvUsrMsg.setLayoutParams(params);//

        }
        setRemoveChat();

    }
    public void setRemoveChat(){
        if(userItemAdapter != null){
            userItemAdapter.setIListenerRemoveChatUser(new UserMsgAdapter.IListenerRemoveChatUser() {
                @Override
                public void OnListenerRemove(int pst) {
                    userItemHrzAdapter.notifyItemRemoved(pst);
                }

                @Override
                public void OnListenerUndo(int pst) {
                    userItemHrzAdapter.notifyItemInserted(pst);
                    msgBinding.rvUsrMsg.scrollToPosition(pst);
                }
            });
        }
    }
}