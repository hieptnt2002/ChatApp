package com.realtime.message.view.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.realtime.message.R;
import com.realtime.message.databinding.ItemUserBinding;
import com.realtime.message.databinding.ItemUserHrzBinding;
import com.realtime.message.model.User;
import com.realtime.message.view.activity.MessageActivity;
import com.realtime.message.view.activity.SplashScreen;
import com.realtime.message.viewmodel.UserMsgAdpViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserMsgAdapter extends RecyclerView.Adapter<UserMsgAdapter.ViewHolder> {
    private List<User> mList;
    Context mContext;
    int SR;
    ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface IListenerRemoveChatUser {
        void OnListenerRemove( int pst);
        void OnListenerUndo(int pst);
    }


    IListenerRemoveChatUser mListener;

    public void setIListenerRemoveChatUser(IListenerRemoveChatUser mListener) {
        this.mListener = mListener;
    }

    public UserMsgAdapter(Context context, List<User> mList, int SR) {
        this.mList = mList;
        this.SR = SR;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (SR == 0) {
            ItemUserBinding itemUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user, parent, false);
            return new ViewHolder(itemUserBinding);
        } else {
            ItemUserHrzBinding itemUserHrzBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_hrz, parent, false);
            return new ViewHolder(itemUserHrzBinding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mList.get(position);
        Collections.sort(mList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        if (SR == 0) {
            holder.itemUserBinding.setUserMsg(new UserMsgAdpViewModel(user.getUid()));
            holder.itemUserBinding.setUser(user);
            holder.itemUserBinding.imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("user", user.getUid());
                    mContext.startActivity(intent);
                }
            });
            viewBinderHelper.bind(holder.itemUserBinding.swipeRevealLayout, user.getUid());
            holder.itemUserBinding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialogLogout = new AlertDialog.Builder(mContext);
                    dialogLogout.setTitle("Xóa đoạn chat");
                    dialogLogout.setCancelable(false);
                    dialogLogout.setIcon(R.drawable.delete);

                    dialogLogout.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialogLogout.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User rUser = user;
                            int pst = holder.getAdapterPosition();
                            mList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            mListener.OnListenerRemove(pst);

                            Snackbar snackbar = Snackbar.make(holder.itemUserBinding.swipeRevealLayout, user.getName() + " Remove", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mList.add(pst, rUser);
                                    notifyItemInserted(pst);
                                    mListener.OnListenerUndo(pst);
                                }
                            });

                            snackbar.show();
                        }
                    });
                    dialogLogout.create().show();


                }
            });

        } else {
            holder.itemUserHrzBinding.setUser(user);
            holder.itemUserHrzBinding.imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("user", user.getUid());
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (mList != null) return mList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding itemUserBinding;
        ItemUserHrzBinding itemUserHrzBinding;

        public ViewHolder(ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            this.itemUserBinding = itemUserBinding;
        }

        public ViewHolder(ItemUserHrzBinding itemUserHrzBinding) {
            super(itemUserHrzBinding.getRoot());
            this.itemUserHrzBinding = itemUserHrzBinding;
        }
    }
}
