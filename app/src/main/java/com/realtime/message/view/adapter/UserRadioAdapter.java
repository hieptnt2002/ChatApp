package com.realtime.message.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.realtime.message.R;
import com.realtime.message.databinding.ItemUserRadioBinding;
import com.realtime.message.viewmodel.UserRadioViewModel;

import java.util.List;

public class UserRadioAdapter extends RecyclerView.Adapter<UserRadioAdapter.ViewHolder> {
    private List<UserRadioViewModel> mList;
    Context mContext;
    public ISelectedUserIF mListener;

    private boolean isSelected = false;


    public interface ISelectedUserIF {
        void onSelected(UserRadioViewModel user);

        void onRemoveSelected(UserRadioViewModel user);
    }
    public void setISelectedUser(ISelectedUserIF mListener){
        this.mListener = mListener;
    }

    public UserRadioAdapter(Context context, List<UserRadioViewModel> mList) {
        this.mList = mList;
        this.mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemUserRadioBinding radioBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_radio, parent, false);
        return new ViewHolder(radioBinding);


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserRadioViewModel user = mList.get(position);

        holder.radioBinding.setUserRadio(user);
        holder.radioBinding.layoutGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelected = true;
                if (holder.radioBinding.btnRadioUser.isChecked()) {
                    holder.radioBinding.btnRadioUser.setChecked(false);
                    user.setChecked(false);
                    mListener.onRemoveSelected(user);

                } else {
                    holder.radioBinding.btnRadioUser.setChecked(true);
                    mListener.onSelected(user);
                    user.setChecked(true);
                }
                isSelected = false;

            }
        });
        holder.radioBinding.btnRadioUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isSelected){
                    if (isChecked) {
                        mListener.onSelected(user);
                        user.setChecked(true);
                    } else {
                        mListener.onRemoveSelected(user);
                        user.setChecked(false);
                    }
                }

            }
        });
        isSelected = false;

    }


    @Override
    public int getItemCount() {
        if (mList != null) return mList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserRadioBinding radioBinding;


        public ViewHolder(ItemUserRadioBinding radioBinding) {
            super(radioBinding.getRoot());
            this.radioBinding = radioBinding;
        }
    }
}
