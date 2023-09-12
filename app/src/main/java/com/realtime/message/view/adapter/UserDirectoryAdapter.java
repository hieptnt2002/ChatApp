package com.realtime.message.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.realtime.message.R;
import com.realtime.message.databinding.ItemUserDirectoryBinding;
import com.realtime.message.model.User;
import com.realtime.message.view.activity.MessageActivity;

import java.util.List;

public class UserDirectoryAdapter extends RecyclerView.Adapter<UserDirectoryAdapter.ViewHolder> {
    private List<User> mList;
    Context mContext;

    public UserDirectoryAdapter(Context context, List<User> mList) {
        this.mList = mList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserDirectoryBinding itemUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_directory, parent, false);
        return new ViewHolder(itemUserBinding);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mList.get(position);
        holder.itemUserBinding.setUser(user);
        holder.itemUserBinding.layoutMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("user", user.getUid());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mList != null) return mList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserDirectoryBinding itemUserBinding;

        public ViewHolder(ItemUserDirectoryBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            this.itemUserBinding = itemUserBinding;
        }

    }
}
