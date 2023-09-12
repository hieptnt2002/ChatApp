package com.realtime.message.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.realtime.message.R;
import com.realtime.message.databinding.ActivityMesssageBinding;
import com.realtime.message.model.Message;
import com.realtime.message.view.adapter.MsgAdapter;
import com.realtime.message.viewmodel.MessageViewModel;

import java.util.List;

public class MessageActivity extends AppCompatActivity {
    ActivityMesssageBinding messsageBinding;
    String urlImg;
    String uid = "";
    MsgAdapter msgAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messsage);
        messsageBinding = DataBindingUtil.setContentView(this, R.layout.activity_messsage);

        uid = getIntent().getStringExtra("user");
        if (uid != null) {

            MessageViewModel messageViewModel = new MessageViewModel(uid, this);
            messsageBinding.setMessageViewModel(messageViewModel);
            chat();
            messsageBinding.getMessageViewModel().getMessageFromRealtimeDb();
            messsageBinding.getMessageViewModel().statusReceiver();
        }
    }

    private void chat() {
        messsageBinding.recycleViewMsg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        messsageBinding.getMessageViewModel().getMessageMutableLiveData().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                urlImg = messsageBinding.getMessageViewModel().avatar.get();
                msgAdapter = new MsgAdapter(MessageActivity.this, messages, urlImg);
                messsageBinding.recycleViewMsg.setAdapter(msgAdapter);
                messsageBinding.recycleViewMsg.scrollToPosition(messages.size() - 1);

            }
        });
            }

    @Override
    protected void onResume() {
        super.onResume();
        messsageBinding.getMessageViewModel().status("online");
        messsageBinding.getMessageViewModel().currentUser(uid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        messsageBinding.getMessageViewModel().status("offline");
        messsageBinding.getMessageViewModel().onPause();
        messsageBinding.getMessageViewModel().currentUser("none");
    }
}