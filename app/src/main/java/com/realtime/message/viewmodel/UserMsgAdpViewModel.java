package com.realtime.message.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.realtime.message.model.Message;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UserMsgAdpViewModel {
    String ruid;
    public ObservableField<String> messageLast = new ObservableField<>();
    public ObservableField<String> timeLast = new ObservableField<>();
    public ObservableBoolean isSeen = new ObservableBoolean();
    public ObservableField<String> unread = new ObservableField<>();

    String isMessage;
    String time;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date lastDate;
    Date currentDate;

    public UserMsgAdpViewModel(String ruid) {
        this.ruid = ruid;
        getMessageLast();
    }

    public void getMessageLast() {
        unread.set("");
        timeLast.set("");
        isMessage = "default";
        isSeen.set(false);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message.getSender().equals(firebaseUser.getUid()) && message.getReceiver().equals(ruid) ||
                            message.getSender().equals(ruid) && message.getReceiver().equals(firebaseUser.getUid())) {
                        isMessage = message.getMessage();
                        if ((message.getIsseen() && message.getReceiver().equals(firebaseUser.getUid())) || message.getSender().equals(firebaseUser.getUid())) {
                            isSeen.set(true);
                        } else {
                            isSeen.set(false);
                            count++;
                        }
                        time = message.getTime();
                        try {
                            lastDate = sdf.parse(message.getDate());
                            currentDate = new Date();
                            currentDate = sdf.parse(sdf.format(currentDate));

                            // Chuyển đổi khoảng cách từ milliseconds sang ngày
                            long differenceInDays = TimeUnit.MILLISECONDS.toDays(currentDate.getTime() - lastDate.getTime());

                            if (differenceInDays == 0) {
                                timeLast.set(time.substring(0, 5));
                            } else if (differenceInDays >= 1 && differenceInDays <= 7 || differenceInDays <= -1 && differenceInDays >= -7) {
//                                Locale locale = new Locale("vi");
                                DateFormat dateFormat = new SimpleDateFormat("E");
                                timeLast.set(dateFormat.format(lastDate));
                            } else if (differenceInDays < 365 || differenceInDays > -365) {
                                timeLast.set(message.getDate().substring(0, 5));
                            } else timeLast.set(message.getDate());



                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }

                if (count > 0) unread.set(String.valueOf(count));

                switch (isMessage) {
                    case "default":
                        messageLast.set("No message");
                        break;
                    default:
                        messageLast.set(isMessage);
                        break;
                }
                isMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
