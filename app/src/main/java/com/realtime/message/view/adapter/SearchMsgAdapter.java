package com.realtime.message.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.realtime.message.R;
import com.realtime.message.databinding.ItemUserHrzBinding;
import com.realtime.message.databinding.ItemUserSearchBinding;
import com.realtime.message.model.User;
import com.realtime.message.view.activity.MessageActivity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchMsgAdapter extends RecyclerView.Adapter<SearchMsgAdapter.ViewHolder> {
    private List<User> mList;
    Context mContext;
    int SR;
    private String filterColor;
    public interface ItemClickAddHistorySearch{
        void addSearch(User user);
    }

    public void updateFilterColor(String filterColor){
        this.filterColor = filterColor;
    }
    ItemClickAddHistorySearch historySearch;
    public void setHistorySearch( ItemClickAddHistorySearch historySearch){
        this.historySearch = historySearch;
    }

    public SearchMsgAdapter(Context context, List<User> mList, int SR) {
        this.mList = mList;
        this.SR = SR;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (SR == 0) {
            ItemUserSearchBinding itemUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_search, parent, false);
            return new ViewHolder(itemUserBinding);
        } else {
            ItemUserHrzBinding itemUserHrzBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_hrz, parent, false);
            return new ViewHolder(itemUserHrzBinding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mList.get(position);
        if (SR == 0) {
            holder.itemUserBinding.setUser(user);
            holder.itemUserBinding.imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    historySearch.addSearch(user);
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("user",user.getUid());
                    mContext.startActivity(intent);
                }
            });
            if(filterColor != null){
                Pattern pattern = Pattern.compile(filterColor,Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(user.getName());
                SpannableStringBuilder ssp = new SpannableStringBuilder(user.getName());
                while (matcher.find()){
                    ForegroundColorSpan color = new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.blue));
                    ssp.setSpan(color,matcher.start(),matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                }
                holder.itemUserBinding.textViewUsr.setText(ssp);


            }

        } else {

            holder.itemUserHrzBinding.setUser(user);
            holder.itemUserHrzBinding.imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("user",user.getUid());
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
        ItemUserSearchBinding itemUserBinding;
        ItemUserHrzBinding itemUserHrzBinding;

        public ViewHolder(ItemUserSearchBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            this.itemUserBinding = itemUserBinding;
        }

        public ViewHolder(ItemUserHrzBinding itemUserHrzBinding) {
            super(itemUserHrzBinding.getRoot());
            this.itemUserHrzBinding = itemUserHrzBinding;
        }
    }
}
