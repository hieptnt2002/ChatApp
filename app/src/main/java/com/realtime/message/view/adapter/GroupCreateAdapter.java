package com.realtime.message.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.realtime.message.R;
import com.realtime.message.databinding.ItemUserGroupCreateBinding;
import com.realtime.message.model.User;

import java.util.List;

public class GroupCreateAdapter extends RecyclerView.Adapter<GroupCreateAdapter.ViewHolder> {
    private List<User> mList;
    Context mContext;

    public GroupCreateAdapter(Context context, List<User> mList) {
        this.mList = mList;
        this.mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserGroupCreateBinding groupBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_group_create, parent, false);
        return new ViewHolder(groupBinding);


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mList.get(position);
        holder.groupBinding.setUser(user);
    }


    @Override
    public int getItemCount() {
        if (mList != null) return mList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserGroupCreateBinding groupBinding;


        public ViewHolder(ItemUserGroupCreateBinding groupBinding) {
            super(groupBinding.getRoot());
            this.groupBinding = groupBinding;
        }
    }
}
