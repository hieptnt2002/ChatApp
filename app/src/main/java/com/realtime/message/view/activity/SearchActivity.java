package com.realtime.message.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.realtime.message.R;
import com.realtime.message.databinding.ActivitySearchBinding;
import com.realtime.message.model.User;
import com.realtime.message.view.adapter.SearchMsgAdapter;
import com.realtime.message.viewmodel.SearchViewModel;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding searchBinding;
    SearchMsgAdapter userHistorySearchAdapter, userSearchAdapter;
    SearchViewModel searchViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        searchBinding.getRoot().post(() -> {
            searchBinding.autoCompleteTextSearch.requestFocus();
//            getActivity().getWindow().setSoftInputMode(
//                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
//            );
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        });


        searchBinding.rvUserMsg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchBinding.rvUserMsg.setNestedScrollingEnabled(false);

        searchViewModel = new SearchViewModel();
        searchBinding.setSearchViewModel(searchViewModel);


        searchBinding.rvSuggestion.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        searchBinding.getSearchViewModel().getUserSearchHistory();
        searchBinding.getSearchViewModel().getUserSearchHistoryLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    userHistorySearchAdapter = new SearchMsgAdapter(SearchActivity.this, users, 1);
                    searchBinding.rvSuggestion.setAdapter(userHistorySearchAdapter);

                }
            }
        });


        searchBinding.getSearchViewModel().getUserMutableLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                setAdapterRv(users);
            }
        });
        searchBinding.getSearchViewModel().getUserFromRealtimeDatabase("");
        searchBinding.autoCompleteTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBinding.getSearchViewModel().getUserFromRealtimeDatabase(s.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        userSearchAdapter.updateFilterColor(s.toString());
                        userSearchAdapter.notifyDataSetChanged();
                    }
                },1000);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setAdapterRv(List<User> mList) {
        userSearchAdapter = new SearchMsgAdapter(this, mList, 0);
        userSearchAdapter.setHistorySearch(new SearchMsgAdapter.ItemClickAddHistorySearch() {
            @Override
            public void addSearch(User user) {
                if (user != null)
                    searchBinding.getSearchViewModel().addHtrSearchRealtimeDb(user);
            }
        });

        searchBinding.rvUserMsg.setAdapter(userSearchAdapter);
        // Tính toán tổng chiều cao của các item

        if (userSearchAdapter != null) {
            int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_user);
            int numItems = 0;
            numItems = userSearchAdapter.getItemCount(); // số lượng item trong RecyclerView
            int totalHeight = itemHeight * numItems; // tổng chiều cao của tất cả các item trong RecyclerView
            ViewGroup.LayoutParams params = searchBinding.rvUserMsg.getLayoutParams();
            params.height = totalHeight;
            searchBinding.rvUserMsg.setLayoutParams(params);//

        }

    }
}
