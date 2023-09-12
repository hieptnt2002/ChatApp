package com.realtime.message.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.realtime.message.R;
import com.realtime.message.databinding.ActivityUserGroupSelectorBinding;
import com.realtime.message.model.User;
import com.realtime.message.view.adapter.SelectedUserAdapter;
import com.realtime.message.view.adapter.UserRadioAdapter;
import com.realtime.message.viewmodel.UserGroupSelectorViewModel;
import com.realtime.message.viewmodel.UserRadioViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserGroupSelectorActivity extends AppCompatActivity {
    ActivityUserGroupSelectorBinding groupBinding;
    RecyclerView rvUser, rvUserSelected;
    UserRadioAdapter groupAdapter;
    SelectedUserAdapter selectedUserAdapter;
    EditText edtSearch;
    List<User> selectedUserList;
    List<UserRadioViewModel> userList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_group_selector);
        groupBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_group_selector);
        rvUser = groupBinding.rvUserMsg;
        edtSearch = groupBinding.edtSearch;
        rvUserSelected = groupBinding.rvSelectedUser;

        rvUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvUser.setHasFixedSize(true);


        UserGroupSelectorViewModel groupViewModel = new UserGroupSelectorViewModel(this);
        groupBinding.setGroupModel(groupViewModel);
        groupBinding.getGroupModel().getUserFromRealtimeDatabase("");
        userList = new ArrayList<>();
        selectedUserList = new ArrayList<>();
        groupBinding.getGroupModel().getUserMutableLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userList.clear();
                for (int i = 0; i < users.size(); i++) {
                    userList.add(new UserRadioViewModel(users.get(i), false));
                }


                groupAdapter = new UserRadioAdapter(UserGroupSelectorActivity.this, userList);
                rvUser.setAdapter(groupAdapter);
                groupAdapter.setISelectedUser(new UserRadioAdapter.ISelectedUserIF() {
                    @Override
                    public void onSelected(UserRadioViewModel user) {
                        selectedUserList.add(user.getUser());
                        setVisibilityLayoutSelectedBottom();
                        groupBinding.getGroupModel().getUserGroupLiveData().setValue(selectedUserList);
                    }

                    @Override
                    public void onRemoveSelected(UserRadioViewModel user) {
                        selectedUserList.remove(user.getUser());
                        setVisibilityLayoutSelectedBottom();
                        groupBinding.getGroupModel().getUserGroupLiveData().setValue(selectedUserList);
                    }

                });


            }
        });
        userSelectedToGroup();
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                groupBinding.getGroupModel().getUserFromRealtimeDatabase(s.toString());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void userSelectedToGroup() {
        groupBinding.getGroupModel().getUserGroupLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userGroupList) {
                selectedUserAdapter = new SelectedUserAdapter(UserGroupSelectorActivity.this, userGroupList);
                rvUserSelected.setLayoutManager(new LinearLayoutManager(UserGroupSelectorActivity.this, LinearLayoutManager.HORIZONTAL, false));
                rvUserSelected.setHasFixedSize(true);
                rvUserSelected.setAdapter(selectedUserAdapter);

                selectedUserAdapter.setIRemoveSelectedUserIF(new SelectedUserAdapter.IRemoveSelectedUserIF() {
                    @Override
                    public void onRemoveChecked(User user) {
                        if (user != null)
                            for (int i = 0; i < userList.size(); i++) {
                                if (user.getUid().equals(userList.get(i).getUser().getUid())) {
                                    userList.set(i, new UserRadioViewModel(user, false));
                                    groupAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                        setVisibilityLayoutSelectedBottom();
                    }
                });
            }
        });
    }

    public void setVisibilityLayoutSelectedBottom() {
        if (!selectedUserList.isEmpty()) {
            groupBinding.layoutUserSelected.setVisibility(View.VISIBLE);
            if (selectedUserList.size() == 1) {
                Animation animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.visible_up);
                groupBinding.layoutUserSelected.startAnimation(animSlideUp);
            }
        } else {
            groupBinding.layoutUserSelected.setVisibility(View.GONE);
            Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.gone_down);
            groupBinding.layoutUserSelected.startAnimation(animSlideDown);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CANCELED) {
            return;
        } else {
            groupBinding.getGroupModel().onActivityResult(requestCode, resultCode, data);
        }
    }
}
