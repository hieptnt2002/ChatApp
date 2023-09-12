package com.realtime.message.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.realtime.message.R;
import com.realtime.message.databinding.ItemUserSelectedBinding;
import com.realtime.message.model.User;

import java.util.List;

public class SelectedUserAdapter extends RecyclerView.Adapter<SelectedUserAdapter.ViewHolder> {
    private List<User> mList;
    Context mContext;
    IRemoveSelectedUserIF mListener;

    public interface IRemoveSelectedUserIF {
        void onRemoveChecked(User user);
    }

    public void setIRemoveSelectedUserIF(IRemoveSelectedUserIF mListener) {
        this.mListener = mListener;
    }

    public SelectedUserAdapter(Context context, List<User> mList) {
        this.mList = mList;
        this.mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemUserSelectedBinding hrzBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_selected, parent, false);
        return new ViewHolder(hrzBinding);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mList.get(position);
        holder.hrzBinding.setUser(user);
        holder.hrzBinding.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = holder.getAdapterPosition();
               if( i != RecyclerView.NO_POSITION){
                   mList.remove(i);
                   notifyItemRemoved(i);
                   notifyItemChanged(i);
                   mListener.onRemoveChecked(user);
               }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList != null) return mList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserSelectedBinding hrzBinding;


        public ViewHolder(ItemUserSelectedBinding hrzBinding) {
            super(hrzBinding.getRoot());
            this.hrzBinding = hrzBinding;
        }
    }
}
