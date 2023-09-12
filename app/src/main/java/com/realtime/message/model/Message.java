package com.realtime.message.model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    String sender;
    String receiver;
    String message;
    Boolean isseen;
    String time;

    String date;

    Date currentDate = new Date();

    public Message() {
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsseen() {
        return isseen;
    }

    public void setIsseen(Boolean isseen) {
        this.isseen = isseen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date chatDate  = sdf.parse(date);
            if (currentDate.getDate() == chatDate.getDate()) return time.substring(0, 5);
            else return time.substring(0, 5) + ", " + date;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
