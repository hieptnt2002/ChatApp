package com.realtime.message.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.realtime.message.R;
import com.realtime.message.databinding.ActivityGroupCreateBinding;
import com.realtime.message.model.User;
import com.realtime.message.view.adapter.GroupCreateAdapter;
import com.realtime.message.viewmodel.GroupCreateViewModel;

import java.util.List;

public class GroupCreateActivity extends AppCompatActivity {
    ActivityGroupCreateBinding groupCreateBinding;
    RecyclerView rvUserGroup;
    GroupCreateAdapter groupCreateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        groupCreateBinding = DataBindingUtil.setContentView(this, R.layout.activity_group_create);
        rvUserGroup = groupCreateBinding.rvUserGroup;
        rvUserGroup.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Intent intent = getIntent();

        GroupCreateViewModel groupCreateViewModel = new GroupCreateViewModel(this);
        groupCreateBinding.setGroupModel(groupCreateViewModel);
        groupCreateBinding.getGroupModel().setDataFromIntent(intent);

        groupCreateBinding.getGroupModel().getUserGroupLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userList) {
                groupCreateAdapter = new GroupCreateAdapter(GroupCreateActivity.this, userList);
                rvUserGroup.setAdapter(groupCreateAdapter);
                for (int i =0;i<userList.size();i++){
                    Toast.makeText(GroupCreateActivity.this, userList.get(i).getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}