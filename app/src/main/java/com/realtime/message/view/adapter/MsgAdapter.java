package com.realtime.message.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.realtime.message.R;
import com.realtime.message.databinding.ItemMsgLeftBinding;
import com.realtime.message.databinding.ItemMsgRightBinding;
import com.realtime.message.model.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Message> mList;
    Context mContext;
    String imgRcv;
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    FirebaseUser firebaseUser;
    SimpleDateFormat sdf;
    Map<String, Date> map = new HashMap<>();

    public MsgAdapter(Context context, List<Message> mList, String imgRcv) {
        this.mList = mList;
        this.imgRcv = imgRcv;
        this.mContext = context;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            ItemMsgLeftBinding msgLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_msg_left, parent, false);
            return new ViewHolder(msgLeftBinding);
        } else {
            ItemMsgRightBinding msgRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_msg_right, parent, false);
            return new ViewHolder(msgRightBinding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mList.get(position);

        if (holder.getItemViewType() == MSG_TYPE_LEFT) {
            holder.msgLeftBinding.setMessage(message);
            Glide.with(mContext).load(imgRcv).error(mContext.getResources().getDrawable(R.drawable.noimg)).into(holder.msgLeftBinding.imageView2);
        } else
            holder.msgRightBinding.setMessage(message);

        if (position == mList.size() - 1) {
            if (mList.get(position).getSender().equals(firebaseUser.getUid()) && holder.getItemViewType() == MSG_TYPE_RIGHT) {
                holder.msgRightBinding.seen.setVisibility(View.VISIBLE);
            } else if (mList.get(position).getReceiver().equals(firebaseUser.getUid())) {
                holder.msgLeftBinding.seen.setVisibility(View.VISIBLE);
            }
        } else if (holder.getItemViewType() == MSG_TYPE_LEFT) {
            holder.msgLeftBinding.seen.setVisibility(View.GONE);
        } else
            holder.msgRightBinding.seen.setVisibility(View.GONE);


        try {
            Date gapDate = sdf.parse(mList.get(0).getDate());
            if (map.isEmpty()) {
                map.put(mList.get(0).getTime() + mList.get(0).getDate(), gapDate);
                for (int i = 0; i < mList.size(); i++) {
                    Date nextDate = sdf.parse(mList.get(i).getDate());
                    int distance = nextDate.compareTo(gapDate);
                    if (distance == 1) {
                        map.put(mList.get(i).getTime() + mList.get(i).getDate(), nextDate);
                        gapDate = nextDate;
                    }
                }
            }
            if (map.containsKey(message.getTime() + message.getDate()) && map.containsValue(sdf.parse(message.getDate()))) {
                if (holder.getItemViewType() == MSG_TYPE_LEFT) {
                    holder.msgLeftBinding.textViewDate.setVisibility(View.VISIBLE);
                } else {
                    holder.msgRightBinding.textViewDate.setVisibility(View.VISIBLE);
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) return mList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMsgRightBinding msgRightBinding;
        ItemMsgLeftBinding msgLeftBinding;

        public ViewHolder(ItemMsgRightBinding msgRightBinding) {
            super(msgRightBinding.getRoot());
            this.msgRightBinding = msgRightBinding;
        }

        public ViewHolder(ItemMsgLeftBinding msgLeftBinding) {
            super(msgLeftBinding.getRoot());
            this.msgLeftBinding = msgLeftBinding;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (mList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else return MSG_TYPE_LEFT;
    }
}
