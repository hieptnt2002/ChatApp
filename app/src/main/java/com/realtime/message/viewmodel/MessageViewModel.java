package com.realtime.message.viewmodel;

import static com.realtime.message.constant.Constant.STATUS_NOTIFICATION;
import static com.realtime.message.constant.Constant.TOGGLE_NOTIFICATION;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.realtime.message.BR;
import com.realtime.message.R;
import com.realtime.message.model.Message;
import com.realtime.message.model.User;
import com.realtime.message.notification.Client;
import com.realtime.message.notification.Data;
import com.realtime.message.notification.MyResponse;
import com.realtime.message.notification.Sender;
import com.realtime.message.remote.APIService;
import com.realtime.message.view.activity.MessageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageViewModel extends BaseObservable {
    String receiver;
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> avatar = new ObservableField<>();
    String send_msg;
    FirebaseUser firebaseUser;
    MutableLiveData<List<Message>> messageMutableLiveData = new MutableLiveData<>();
    DatabaseReference referenceUser;
    public ObservableBoolean isOnline = new ObservableBoolean();
    ValueEventListener seenListener;
    DatabaseReference chatRef;

    APIService apiService;
    Context context;
    boolean notifi = false;


    public static final String TAG = MessageActivity.class.getName();

    public MutableLiveData<List<Message>> getMessageMutableLiveData() {
        return messageMutableLiveData;
    }

    public MessageViewModel(String receiver, Context context) {
        this.receiver = receiver;
        getUserReceiverMsg(receiver);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceUser = FirebaseDatabase.getInstance().getReference("user");
        isOnline.set(false);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        this.context = context;
    }

    public void getUserReceiverMsg(String uid) {
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("user").child(uid);
        receiverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                avatar.set(user.getAvatar());
                name.set(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Bindable
    public String getSend_msg() {
        return send_msg;
    }

    public void setSend_msg(String send_msg) {
        this.send_msg = send_msg;
        notifyPropertyChanged(BR.send_msg);
    }

    @BindingAdapter({"imgUrl", "urlError"})
    public static void loadImage(CircleImageView img, String url, Drawable error) {
        Glide.with(img.getContext()).load(url).error(error).into(img);
    }

    public void statusReceiver() {
        referenceUser.child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getStatus().equals("online"))
                    isOnline.set(true);
                else isOnline.set(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void status(String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        referenceUser.child(firebaseUser.getUid()).updateChildren(map);
    }

    public void currentUser(String uid) {
        SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("currentuser", uid);
        editor.apply();
    }

    public void onPause() {
        chatRef.removeEventListener(seenListener);
    }


    public void getMessageFromRealtimeDb() {
        List<Message> messageList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message.getSender().equals(firebaseUser.getUid()) && message.getReceiver().equals(receiver) ||
                        message.getSender().equals(receiver) && message.getReceiver().equals(firebaseUser.getUid())) {
                    messageList.add(message);
                }
                messageMutableLiveData.setValue(messageList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                for (int i = 0; i < messageList.size(); i++) {
                    if (message.getSender().equals(messageList.get(i).getSender()) && message.getReceiver().equals(messageList.get(i).getReceiver())
                            && message.getDate().equals(messageList.get(i).getDate()) && message.getTime().equals(messageList.get(i).getTime())) {
                        messageList.set(i, message);

                    }
                }
                messageMutableLiveData.setValue(messageList);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage();

    }

    public void seenMessage() {
        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ss : snapshot.getChildren()) {
                    Message chat = ss.getValue(Message.class);
                    if (chat.getSender().equals(receiver) && chat.getReceiver().equals(firebaseUser.getUid())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("isseen", true);
                        ss.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void onClickSendMsg() {
        notifi = true;
        if (send_msg != null && !send_msg.isEmpty()) {
            // Thêm tin nhắn vào cơ sở dữ liệu
            Map<String, Object> map = new HashMap<>();
            map.put("sender", firebaseUser.getUid());
            map.put("receiver", receiver);
            map.put("message", send_msg);
            map.put("isseen", false);
            map.put("time", getTime());
            map.put("date", getDate());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
            reference.push().setValue(map);

//            Notifi
            final String msg = send_msg;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
            ref.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    SharedPreferences sp = context.getSharedPreferences(TOGGLE_NOTIFICATION, Context.MODE_PRIVATE);
                    int enDisNotify = sp.getInt(STATUS_NOTIFICATION, 0);
                    if (notifi && enDisNotify == 0) sendNotification(receiver, user.getName(), msg);
                    notifi = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // Xóa nội dung tin nhắn sau khi đã gửi
            setSend_msg("");

            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(firebaseUser.getUid()).child(receiver);
            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        chatRef.child("id").setValue(receiver);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference chatReceiverRef = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(receiver).child(firebaseUser.getUid());
            chatReceiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        chatReceiverRef.child("id").setValue(firebaseUser.getUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void sendNotification(String receiver, String name, String msg) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens").child(receiver);
        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.getValue(String.class);
                Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, name + ": " + msg, "New Message", receiver);
                Sender sender = new Sender(data, token);
                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().success != 1) {
                                Log.e(TAG, "Notification message failed because not logged in");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getTime() {
        Date date = new Date();
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdfTime.format(date);
        return currentTime;
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdfDate.format(date);
        return currentDate;
    }
}
